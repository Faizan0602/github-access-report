package com.assignment.githubreport.controller;

import com.assignment.githubreport.dto.GithubReportResponse;
import com.assignment.githubreport.service.GithubReportService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/github")
public class GithubReportController {

    private final GithubReportService githubReportService;

    public GithubReportController(GithubReportService githubReportService) {
        this.githubReportService = githubReportService;
    }

    @GetMapping("/report")
    public GithubReportResponse generateReport(@RequestParam("org") @NotBlank String org) {
        return githubReportService.generateOrganizationAccessReport(org.trim());
    }
}
