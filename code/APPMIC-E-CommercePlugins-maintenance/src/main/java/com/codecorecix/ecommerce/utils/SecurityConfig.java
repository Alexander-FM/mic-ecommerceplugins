package com.codecorecix.ecommerce.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private static final String SCOPE_WRITE = "SCOPE_write";

  private static final String SCOPE_READ = "SCOPE_read";

  private static final String[] COMMON_PATHS = {"/", "/{id}"};

  public static final String ROOT_PATH = "/";

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.authorizeRequests(authorizeRequests -> authorizeRequests
            .requestMatchers("/api/users/authorized", "/api/users/login", "/api/users").permitAll()
            .requestMatchers(HttpMethod.GET, COMMON_PATHS).hasAnyAuthority(SCOPE_READ, SCOPE_WRITE)
            .requestMatchers(HttpMethod.POST, ROOT_PATH).hasAnyAuthority(SCOPE_WRITE)
            .requestMatchers(HttpMethod.PUT, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
            .requestMatchers(HttpMethod.DELETE, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
            .requestMatchers(HttpMethod.PATCH, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
            .anyRequest().authenticated()
        )
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2Login(oauth2 -> oauth2.loginPage("/oauth2/authorization/maintenance-client"))
        .oauth2Client(Customizer.withDefaults())
        .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
    return http.build();
  }
}
