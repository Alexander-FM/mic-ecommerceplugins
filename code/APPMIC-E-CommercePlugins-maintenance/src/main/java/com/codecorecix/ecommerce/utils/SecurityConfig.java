package com.codecorecix.ecommerce.utils;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
        .requestMatchers("/api/users/authorized", "/api/users/login", "/api/users", "api/products/active", "api/brands/active", "api/categories/active").permitAll()
        .requestMatchers(HttpMethod.GET, COMMON_PATHS).hasAnyAuthority(SCOPE_READ, SCOPE_WRITE)
        .requestMatchers(HttpMethod.POST, ROOT_PATH).hasAnyAuthority(SCOPE_WRITE)
        .requestMatchers(HttpMethod.PUT, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
        .requestMatchers(HttpMethod.DELETE, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
        .requestMatchers(HttpMethod.PATCH, COMMON_PATHS).hasAnyAuthority(SCOPE_WRITE)
        .anyRequest().authenticated()
      )
      //.cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      //.oauth2Login(oauth2 -> oauth2.loginPage("/oauth2/authorization/maintenance-client"))
      //.oauth2Client(Customizer.withDefaults())
      .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
    return http.build();
  }

//  @Bean
//  public CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOrigins(java.util.Arrays.asList(
//      "http://localhost:3000",
//      "http://localhost:4200",
//      "http://localhost"
//    ));
//    configuration.setAllowedMethods(java.util.Arrays.asList(
//      "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
//    ));
//    configuration.setAllowedHeaders(List.of("*"));
//    configuration.setAllowCredentials(true);
//    configuration.setMaxAge(3600L);
//
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }
}
