package com.codecorecix.ecommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private static final String SCOPE_WRITE = "SCOPE_write";

  private static final String SCOPE_READ = "SCOPE_read";

  private static final String[] COMMON_PATHS = {"/", "/{id}"};

  public static final String ROOT_PATH = "/";

  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
        .requestMatchers(HttpMethod.GET, "/api/orders", "/api/orders/{id}").permitAll()
        .requestMatchers(HttpMethod.GET, COMMON_PATHS).hasAnyAuthority(SCOPE_READ, SCOPE_WRITE)
        .requestMatchers(HttpMethod.POST, ROOT_PATH).hasAnyAuthority(SCOPE_WRITE)
        .requestMatchers(HttpMethod.PUT, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
        .requestMatchers(HttpMethod.DELETE, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
        .requestMatchers(HttpMethod.PATCH, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
        .anyRequest().authenticated()
      )
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
    return http.build();
  }
}

