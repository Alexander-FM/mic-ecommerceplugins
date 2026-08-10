package com.codecorecix.ecommerce.client;

import com.codecorecix.ecommerce.config.WebClientFactory;
import com.codecorecix.ecommerce.event.models.CustomerRequestDto;
import com.codecorecix.ecommerce.event.models.CustomerResponseDto;
import com.codecorecix.ecommerce.event.models.RoleResponseDto;
import com.codecorecix.ecommerce.event.models.UserRequestDto;
import com.codecorecix.ecommerce.event.models.UserResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.WebClientErrorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class MaintenanceClient {
  private final WebClient webClient;

  private final WebClientErrorHandler errorHandler;

  public MaintenanceClient(
      final WebClientFactory webClientFactory, // Inyectamos la FÁBRICA
      final WebClientErrorHandler errorHandler,
      @Value("${app.external.maintenance-service-url}") final String maintenanceServiceUrl) {

    log.info("Connecting to Maintenance Service at: {}", maintenanceServiceUrl);
    // Pedimos a la fábrica que nos dé un cliente INTERNO y SEGURO
    this.webClient = webClientFactory.retrieveInternalWebClient(maintenanceServiceUrl);
    this.errorHandler = errorHandler;
  }

  public GenericResponse<RoleResponseDto> findRoleByName(final String roleName) {
    return webClient
        .get()
        .uri("/api/maintenance/roles/internal/name/{roleName}", roleName)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<GenericResponse<RoleResponseDto>>() {
        })
        .block();
  }

  public GenericResponse<UserResponseDto> createUser(final UserRequestDto userRequest) {
    return webClient
        .post()
        .uri("/api/maintenance/users")
        .bodyValue(userRequest)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<GenericResponse<UserResponseDto>>() {
        })
        .block();
  }

  public GenericResponse<CustomerResponseDto> createCustomer(final CustomerRequestDto customerRequest) {
    return webClient
        .post()
        .uri("/api/maintenance/customers")
        .bodyValue(customerRequest)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<GenericResponse<CustomerResponseDto>>() {
        })
        .block();
  }

  public void deleteUserInternal(final Integer userId) {
    log.warn("Enviando petición de borrado por compensación para el usuario ID: {}", userId);
    webClient
        .delete()
        .uri("/api/maintenance/internal/users/{userId}", userId)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<GenericResponse<Void>>() {
        })
        .block();
  }
}
