package com.assignment.githubreport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;

@Configuration
public class AppConfig {

    @Bean
    public WebClient githubWebClient(GithubProperties githubProperties) {
        return WebClient.builder()
                .baseUrl(githubProperties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubProperties.token())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }

    @Bean(name = "githubTaskExecutor")
    public Executor githubTaskExecutor(GithubProperties githubProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(githubProperties.parallelism());
        executor.setMaxPoolSize(githubProperties.parallelism() * 2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("github-api-");
        executor.initialize();
        return executor;
    }
}
