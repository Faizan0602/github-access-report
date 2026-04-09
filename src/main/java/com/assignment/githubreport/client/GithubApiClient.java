package com.assignment.githubreport.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.assignment.githubreport.client.model.GithubCollaboratorResponse;
import com.assignment.githubreport.client.model.GithubRepositoryResponse;
import com.assignment.githubreport.config.GithubProperties;
import com.assignment.githubreport.exception.GithubApiException;

@Component
public class GithubApiClient {

    private static final Logger log = LoggerFactory.getLogger(GithubApiClient.class);
    private static final String LINK_HEADER = "Link";

    private final WebClient githubWebClient;
    private final GithubProperties githubProperties;

    public GithubApiClient(WebClient githubWebClient, GithubProperties githubProperties) {
        this.githubWebClient = githubWebClient;
        this.githubProperties = githubProperties;
    }

    public List<GithubRepositoryResponse> fetchOrganizationRepositories(String org) {
        String firstPageUri = "/orgs/%s/repos?per_page=%d&page=1".formatted(org, githubProperties.perPage());
        return fetchAllPages(firstPageUri, GithubRepositoryResponse.class);
    }

    public List<GithubCollaboratorResponse> fetchRepositoryCollaborators(String org, String repositoryName) {
        String firstPageUri = "/repos/%s/%s/contributors?per_page=%d&page=1".formatted(org, repositoryName, githubProperties.perPage());
        return fetchAllPages(firstPageUri, GithubCollaboratorResponse.class);
    }

    private <T> List<T> fetchAllPages(String initialUri, Class<T> elementType) {
        List<T> allItems = new ArrayList<>();
        String currentUri = initialUri;

        while (currentUri != null) {
            ResponseEntity<List<T>> response = executeGet(currentUri, elementType);
            List<T> pageItems = response.getBody() == null ? List.of() : response.getBody();
            allItems.addAll(pageItems);
            currentUri = extractNextPageUrl(response.getHeaders().getFirst(LINK_HEADER));
        }

        return allItems;
    }

    private <T> ResponseEntity<List<T>> executeGet(String uri, Class<T> elementType) {
        try {
            return githubWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntityList(elementType)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("GitHub API call failed for URI: {}, status: {}, response: {}",
                    uri, ex.getStatusCode().value(), ex.getResponseBodyAsString());
            throw new GithubApiException("GitHub API error while calling " + uri, ex.getStatusCode().value());
        } catch (Exception ex) {
            log.error("Unexpected error while calling GitHub API URI: {}", uri, ex);
            throw new GithubApiException("Unexpected GitHub API error for " + uri, 500);
        }
    }

    private String extractNextPageUrl(String linkHeader) {
        if (linkHeader == null || linkHeader.isBlank()) {
            return null;
        }

        String[] parts = linkHeader.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.contains("rel=\"next\"")) {
                int start = trimmed.indexOf('<');
                int end = trimmed.indexOf('>');
                if (start >= 0 && end > start) {
                    String absoluteUrl = trimmed.substring(start + 1, end);
                    return absoluteUrl.replace(githubProperties.baseUrl(), "");
                }
            }
        }

        return null;
    }
}
