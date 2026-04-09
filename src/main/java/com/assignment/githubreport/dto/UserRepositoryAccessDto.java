package com.assignment.githubreport.dto;

import java.util.List;

public record UserRepositoryAccessDto(
        String username,
        List<RepositoryPermissionDto> repositories
) {
}
