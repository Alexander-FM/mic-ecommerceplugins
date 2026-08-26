package com.codecorecix.ecommerce.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  private final WebClient internalApiBuilderSimple;

  private final WebClient.Builder internalApiBuilderLoadBalanced;

  // Inyectamos la variable. Si no existe (ej. corriendo en local nativo), asume true por defecto
  @Value("${spring.cloud.kubernetes.enabled:true}")
  private boolean isK8sEnabled;

  /**
   * Devuelve el WebClient correcto basándose en sí la URL apunta a un entorno local o de cluster. <p> @param serviceUrl La URL final ya
   * resuelta (ej.: "<a href="http://localhost:8082"> Url local </a>" o "<a href="http://appmic-employees"> Url kubernetes </a>)</p>
   */
  public WebClient retrieveWebClient(final String serviceUrl) {
    if (!isK8sEnabled || isLocalUrl(serviceUrl)) {
      // --- ESCENARIO LOCAL ---
      log.info("Web Client Factory: Detectado Localhost. Usando cliente SIMPLE: {}", serviceUrl);
      return simpleWebClient
          .mutate()
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

  /**
   * Devuelve un WebClient INTERNO y SEGURO que cambia entre local y K8s.
   * Automáticamente, añade el token de Client Credentials.
   */
  public WebClient retrieveInternalWebClient(final String serviceUrl) {
    if (!isK8sEnabled || isLocalUrl(serviceUrl)) {
      // Para local, usamos un cliente simple pero con el filtro OAuth2.
      // Esto permite probar el flujo de token incluso en local.
      log.info("Web Client Factory: Usando cliente INTERNO (simple + oauth2) para URL local: {}", serviceUrl);
      return internalApiBuilderSimple
          .mutate()
          .baseUrl(serviceUrl)
          .build();
    } else {
      // Para K8s, el builder ya es @LoadBalanced y tiene el filtro OAuth2.
      log.info("Web Client Factory: Usando cliente INTERNO (load balanced + oauth2) para URL de K8s: {}", serviceUrl);
      return internalApiBuilderLoadBalanced
          .baseUrl(serviceUrl)
          .build();
    }
  }

  private boolean isLocalUrl(String url) {
    return url.contains("localhost") || url.contains("127.0.0.1");
  }
}
