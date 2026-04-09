package com.assignment.githubreport.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubCollaboratorResponse(
        String login,
        @JsonProperty("permissions") GithubPermissionsResponse permissions
) {
}
