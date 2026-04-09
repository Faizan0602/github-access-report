package com.assignment.githubreport.service;

import com.assignment.githubreport.dto.GithubReportResponse;

public interface GithubReportService {
    GithubReportResponse generateOrganizationAccessReport(String organization);
}
