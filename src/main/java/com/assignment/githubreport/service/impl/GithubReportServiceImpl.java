package com.assignment.githubreport.service.impl;

import com.assignment.githubreport.client.GithubApiClient;
import com.assignment.githubreport.client.model.GithubCollaboratorResponse;
import com.assignment.githubreport.client.model.GithubPermissionsResponse;
import com.assignment.githubreport.client.model.GithubRepositoryResponse;
import com.assignment.githubreport.dto.GithubReportResponse;
import com.assignment.githubreport.dto.RepositoryPermissionDto;
import com.assignment.githubreport.dto.UserRepositoryAccessDto;
import com.assignment.githubreport.service.GithubReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class GithubReportServiceImpl implements GithubReportService {

    private static final Logger log = LoggerFactory.getLogger(GithubReportServiceImpl.class);
    private static final long REPO_COLLABORATOR_FETCH_TIMEOUT_SECONDS = 120;

    private final GithubApiClient githubApiClient;
    private final Executor githubTaskExecutor;

    public GithubReportServiceImpl(
            GithubApiClient githubApiClient,
            @Qualifier("githubTaskExecutor") Executor githubTaskExecutor
    ) {
        this.githubApiClient = githubApiClient;
        this.githubTaskExecutor = githubTaskExecutor;
    }

    @Override
    public GithubReportResponse generateOrganizationAccessReport(String organization) {
        long start = System.currentTimeMillis();
        log.info("Generating GitHub access report for organization: {}", organization);

        List<GithubRepositoryResponse> repositories = githubApiClient.fetchOrganizationRepositories(organization);
        log.info("Found {} repositories for organization: {}", repositories.size(), organization);

        Map<String, List<RepositoryPermissionDto>> userToRepositories = new ConcurrentHashMap<>();

        // Fetch collaborators per repository in parallel to avoid N sequential API roundtrips.
        List<CompletableFuture<Void>> tasks = repositories.stream()
                .map(repository -> CompletableFuture.runAsync(() ->
                        processRepository(organization, repository.name(), userToRepositories), githubTaskExecutor))
                .toList();

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                .orTimeout(REPO_COLLABORATOR_FETCH_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .join();

        List<UserRepositoryAccessDto> users = userToRepositories.entrySet().stream()
                .map(entry -> new UserRepositoryAccessDto(
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparing(RepositoryPermissionDto::repository))
                                .toList()
                ))
                .sorted(Comparator.comparing(UserRepositoryAccessDto::username))
                .toList();

        long durationMs = System.currentTimeMillis() - start;
        log.info("Completed GitHub access report for organization: {} in {} ms", organization, durationMs);

        return new GithubReportResponse(
                organization,
                Instant.now(),
                users.size(),
                repositories.size(),
                users
        );
    }

    private void processRepository(
            String organization,
            String repositoryName,
            Map<String, List<RepositoryPermissionDto>> userToRepositories
    ) {
        List<GithubCollaboratorResponse> collaborators = githubApiClient
                .fetchRepositoryCollaborators(organization, repositoryName);

        for (GithubCollaboratorResponse collaborator : collaborators) {
            String permission = highestPermission(collaborator.permissions());
            userToRepositories.compute(collaborator.login(), (user, repos) -> {
                List<RepositoryPermissionDto> updated = repos == null
                        ? new java.util.ArrayList<>()
                        : new java.util.ArrayList<>(repos);
                updated.add(new RepositoryPermissionDto(repositoryName, permission));
                return updated;
            });
        }

        log.debug("Processed {} collaborators for repository: {}", collaborators.size(), repositoryName);
    }

    private String highestPermission(GithubPermissionsResponse permissions) {
        // Convert GitHub's boolean permission flags into one highest effective role.
        if (permissions == null) {
            return "unknown";
        }
        if (permissions.admin()) {
            return "admin";
        }
        if (permissions.maintain()) {
            return "maintain";
        }
        if (permissions.push()) {
            return "write";
        }
        if (permissions.triage()) {
            return "triage";
        }
        if (permissions.pull()) {
            return "read";
        }
        return "unknown";
    }
}
