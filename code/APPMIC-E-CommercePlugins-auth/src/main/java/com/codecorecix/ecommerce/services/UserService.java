package com.codecorecix.ecommerce.services;

import java.util.List;

import com.codecorecix.ecommerce.api.dto.response.EcommerceUserDetails;
import com.codecorecix.ecommerce.event.models.CustomerResponseDto;
import com.codecorecix.ecommerce.event.models.EmployeeResponseDto;
import com.codecorecix.ecommerce.event.models.UserResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserService implements UserDetailsService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final WebClient.Builder loadBalancedWebClientBuilder;

  private final WebClient simpleWebClient;

  private final Environment env;

  public UserService(final WebClient.Builder loadBalancedWebClientBuilder, final WebClient simpleWebClient, final Environment env) {
    this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
    this.simpleWebClient = simpleWebClient;
    this.env = env;
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
      final WebClient client = getWebClient();
      final String uri = env.getProperty("MS_MAINTENANCE_NAME", "http://127.0.0.1:9090/api/maintenance/users/login");
      final GenericResponse<UserResponseDto> userResponseDto = client
          .get()
          .uri(uri, uriBuilder -> uriBuilder
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
      final String uriClient =
          env.getProperty("MS_MAINTENANCE_CLIENT", "http://127.0.0.1:9090/api/maintenance/customers/username/");
      final GenericResponse<CustomerResponseDto> customerResponseDto = client
          .get()
          .uri(uriClient + username)
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
      final String uriEmployee =
          env.getProperty("MS_MAINTENANCE_EMPLOYEE", "http://127.0.0.1:9090/api/maintenance/employees/username/");
      final GenericResponse<EmployeeResponseDto> employeeResponseDto = client
          .get()
          .uri(uriEmployee + username)
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
      throw new UsernameNotFoundException(StringUtils.join("Error in the login, no exist the user ", e.getMessage()));
    }
  }

  private WebClient getWebClient() {
    if (StringUtils.isBlank(env.getProperty("MS_MAINTENANCE_NAME"))) {
      log.info("Using simpleWebClient as MS_MAINTENANCE_NAME is not set.");
      return simpleWebClient;
    } else {
      log.info("Using loadBalancedWebClient.");
      return loadBalancedWebClientBuilder.build();
    }
  }
}
