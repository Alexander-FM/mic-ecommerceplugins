package com.codecorecix.ecommerce.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
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

  // --- ¡NUEVO BEAN ESPECIALIZADO! ---
  // Este builder es para la comunicación interna y segura SERVICIO-A-SERVICIO
  @Bean(name = "internalApiBuilderSimple") // Un nombre para identificarlo
  public WebClient internalApiClientBuilderSimple(final OAuth2AuthorizedClientManager authorizedClientManager) {

    // 1. Creamos el filtro mágico de OAuth2
    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

    // 2. Le decimos que use nuestro registro de 'client_credentials' del application.yaml del microservicio auth
    oauth2Client.setDefaultClientRegistrationId("maintenance-client-credentials");

    // 3. Devolvemos un BUILDER que ya tiene el filtro aplicado
    return WebClient
        .builder()
        .filter(oauth2Client)
        .build();
  }

  // --- ¡NUEVO BEAN ESPECIALIZADO! ---
  // Este builder es para la comunicación interna y segura SERVICIO-A-SERVICIO
  @Bean(name = "internalApiBuilderLoadBalanced") // Un nombre para identificarlo
  @LoadBalanced // También necesita balanceo de carga en K8s
  public WebClient.Builder internalApiClientBuilderLoadBalanced(final OAuth2AuthorizedClientManager authorizedClientManager) {

    // 1. Creamos el filtro mágico de OAuth2
    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

    // 2. Le decimos que use nuestro registro de 'client_credentials' del application.yaml del microservicio auth
    oauth2Client.setDefaultClientRegistrationId("maintenance-client-credentials");

    // 3. Devolvemos un BUILDER que ya tiene el filtro aplicado
    return WebClient
        .builder()
        .filter(oauth2Client);
  }
}