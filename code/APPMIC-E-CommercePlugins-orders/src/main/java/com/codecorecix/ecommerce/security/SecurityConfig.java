package com.codecorecix.ecommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private static final String ROLE_ADMIN = "ROLE_ADMIN";

  private static final String ROLE_USER = "ROLE_USER";

  private static final String[] COMMON_PATHS = {"/", "/{id}"};

  public static final String ROOT_PATH = "/";

  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
        .requestMatchers(HttpMethod.GET, "/api/orders/public/**").permitAll()
        .requestMatchers(HttpMethod.GET, COMMON_PATHS).hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
        .requestMatchers(HttpMethod.POST, ROOT_PATH).hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
        .requestMatchers(HttpMethod.PUT, COMMON_PATHS).hasAuthority(ROLE_ADMIN)
        .requestMatchers(HttpMethod.DELETE, COMMON_PATHS).hasAuthority(ROLE_ADMIN)
        .requestMatchers(HttpMethod.PATCH, COMMON_PATHS).hasAuthority(ROLE_ADMIN)
        .anyRequest().authenticated()
      )
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    // Le decimos que busque los permisos en la claim "roles"
    grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
    // Eliminamos el prefijo SCOPE_ que pone por defecto para que use ROLE_
    grantedAuthoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}

