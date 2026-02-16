package com.codecorecix.ecommerce.services;

import java.util.List;

import com.codecorecix.ecommerce.event.models.UserResponseDto;
import com.codecorecix.ecommerce.utils.GenericResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
      final WebClient client = getWebClient();
      final String uri = env.getProperty("MS_MAINTENANCE_NAME", "http://127.0.0.1:9090/api/maintenance/users/login");
      final GenericResponse<UserResponseDto> userResponseDto = client.get()
        .uri(uri, uriBuilder -> uriBuilder.queryParam("username", username).build())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<GenericResponse<UserResponseDto>>() {
        })
        .block();
      assert userResponseDto != null;
      List<SimpleGrantedAuthority> authorities = userResponseDto.getBody().getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getDescription()))
        .toList();
      return new User(username, userResponseDto.getBody().getPassword(), true, true, true, true, authorities);
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
