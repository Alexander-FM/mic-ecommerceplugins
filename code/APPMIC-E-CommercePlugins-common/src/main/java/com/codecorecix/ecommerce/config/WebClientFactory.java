package com.codesoft.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
public class WebClientFactory {

  private final WebClient.Builder loadBalancedWebClientBuilder;

  private final WebClient simpleWebClient;

  /**
   * Devuelve el WebClient correcto basándose en sí la URL apunta a un entorno local o de cluster. <p> @param serviceUrl La URL final ya
   * resuelta (ej.: "<a href="http://localhost:8082"> Url local </a>" o "<a href="http://appmic-employees"> Url kubernetes </a>)</p>
   */
  public WebClient retrieveWebClient(final String serviceUrl) {
    if (isLocalUrl(serviceUrl)) {
      // --- ESCENARIO LOCAL ---
      log.info("Web Client Factory: Detectado Localhost. Usando cliente SIMPLE: {}", serviceUrl);
      return simpleWebClient.mutate()
        .baseUrl(serviceUrl)
        .build();
    } else {
      // --- ESCENARIO KUBERNETES ---
      log.info("Web Client Factory: Detectado Servicio K8s. Usando cliente LOAD BALANCED: {}", serviceUrl);
      return loadBalancedWebClientBuilder
        .baseUrl(serviceUrl)
        .build();
    }
  }

  private boolean isLocalUrl(String url) {
    return url.contains("localhost") || url.contains("127.0.0.1");
  }
}
