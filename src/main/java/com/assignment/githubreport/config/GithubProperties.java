package com.assignment.githubreport.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "github")
public record GithubProperties(
        @NotBlank String baseUrl,
        @NotBlank String token,
        @Min(1) @Max(100) int perPage,
        @Min(1) @Max(100) int parallelism
) {
}
