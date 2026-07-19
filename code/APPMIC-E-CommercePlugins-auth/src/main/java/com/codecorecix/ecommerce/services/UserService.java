package com.codecorecix.ecommerce.services;

import java.util.List;

import com.codecorecix.ecommerce.api.dto.response.EcommerceUserDetails;
import com.codecorecix.ecommerce.config.WebClientFactory;
import com.codecorecix.ecommerce.event.models.CustomerResponseDto;
import com.codecorecix.ecommerce.event.models.EmployeeResponseDto;
import com.codecorecix.ecommerce.event.models.UserResponseDto;
import com.codecorecix.ecommerce.exception.AuthMessageEnum;
import com.codecorecix.ecommerce.utils.GenericResponse;
import com.codecorecix.ecommerce.utils.WebClientErrorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class UserService implements UserDetailsService {

  private final WebClient webClient;

  private final WebClientErrorHandler errorHandler;

  public UserService(final WebClientFactory webClientFactory, final WebClientErrorHandler errorHandler,
                     @Value("${app.external.maintenance-service-url}") final String maintenanceServiceUrl) {
    log.info("Connecting to Maintenance Service at: {}", maintenanceServiceUrl);
    this.webClient = webClientFactory.retrieveWebClient(maintenanceServiceUrl);
    this.errorHandler = errorHandler;
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    try {
      List<SimpleGrantedAuthority> authorities = null;
      String password = null;
      Long employeeId = null;
      String employeeName = null;
      Long customerId = null;
      String customerName = null;
      final GenericResponse<UserResponseDto> userResponseDto = webClient
          .get()
          .uri("/api/maintenance/users/login", uriBuilder -> uriBuilder
              .queryParam("username", username)
              .build())
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<GenericResponse<UserResponseDto>>() {
          })
          .block();
      if (userResponseDto != null && userResponseDto.getBody() != null) {
        authorities = userResponseDto
            .getBody()
            .getRoles()
            .stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getDescription()))
            .toList();
        password = userResponseDto
            .getBody()
            .getPassword();
      }
      //Hacemos una llamada al microservicio de cliente para obtener información adicional
      final GenericResponse<CustomerResponseDto> customerResponseDto = webClient
          .get()
          .uri("/api/maintenance/customers/username/" + username)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<GenericResponse<CustomerResponseDto>>() {
          })
          .block();
      if (customerResponseDto != null && customerResponseDto.getBody() != null) {
        final CustomerResponseDto customer = customerResponseDto.getBody();
        customerId = Long.valueOf(customer.getId());
        customerName = customer.getName();
      }
      //Hacemos una llamada al microservicio de empleado para obtener información adicional
      final GenericResponse<EmployeeResponseDto> employeeResponseDto = webClient
          .get()
          .uri("/api/maintenance/employees/username/" + username)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<GenericResponse<EmployeeResponseDto>>() {
          })
          .block();
      if (employeeResponseDto != null && employeeResponseDto.getBody() != null) {
        final EmployeeResponseDto employee = employeeResponseDto.getBody();
        employeeId = Long.valueOf(employee.getId());
        employeeName = employee.getName();
      }
      return new EcommerceUserDetails(customerId, customerName, employeeId, employeeName, username, password, authorities);
    } catch (final RuntimeException e) {
      throw errorHandler.handle(e, AuthMessageEnum.AUTH_EMPLOYEE_SERVICE_UNAVAILABLE);
    }
  }
}
