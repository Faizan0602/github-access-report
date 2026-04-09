package com.assignment.githubreport.client.model;

public record GithubPermissionsResponse(
        boolean pull,
        boolean triage,
        boolean push,
        boolean maintain,
        boolean admin
) {
}
