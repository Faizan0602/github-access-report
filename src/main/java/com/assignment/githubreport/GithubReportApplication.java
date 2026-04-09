package com.assignment.githubreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GithubReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubReportApplication.class, args);
    }
}
