package com.codecorecix.ecommerce.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
public class WebClientConfig {

  @LoadBalanced
  @Bean
  WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  WebClient simpleWebClient() {
    return WebClient
        .builder()
        .build();
  }
}