package com.assignment.githubreport.dto;

import java.time.Instant;
import java.util.List;

public record GithubReportResponse(
        String organization,
        Instant generatedAt,
        int totalUsers,
        int totalRepositories,
        List<UserRepositoryAccessDto> users
) {
}
