package com.codecorecix.ecommerce.security;

import org.springframework.beans.factory.annotation.Value;
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

  // Inyección de todos los endpoints desde application.yml
  @Value("${app.endpoints.order-info}")
  private String orderPath;

  @Value("${app.endpoints.order-status}")
  private String orderStatusPath;

  @Value("${app.endpoints.order-history}") // <-- INYECTADO: Ruta para el historial de órdenes
  private String orderHistoryPath;

  private static final String ROLE_ADMIN = "ROLE_ADMIN";

  private static final String ROLE_USER = "ROLE_USER";

  public static final String ID = "/{id}";

  private final CustomAccessDeniedHandler accessDeniedHandler;

  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler,
                        CustomAuthenticationEntryPoint authenticationEntryPoint) {
    this.accessDeniedHandler = accessDeniedHandler;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    final String orderPathWithId = String.join("", orderPath, ID);
    final String orderStatusPathWithId = String.join("", orderStatusPath, ID);
    final String orderHistorySearchByOrderIdPath = String.join("", orderHistoryPath, ID);

    http
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            //1. Rutas públicas (sin autenticación)
            .requestMatchers(HttpMethod.GET, "/api/orders/public/**")
            .permitAll()
            //2. Rutas protegidas (con autenticación y autorización)
            .requestMatchers(HttpMethod.GET, orderPath, orderStatusPath, orderHistorySearchByOrderIdPath)
            .hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
            .requestMatchers(HttpMethod.POST, orderPath, orderStatusPath, orderHistorySearchByOrderIdPath)
            .hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
            //3. Rutas de administración (solo para ADMIN)
            .requestMatchers(HttpMethod.PUT, orderPathWithId, orderStatusPathWithId, orderHistorySearchByOrderIdPath)
            .hasAuthority(ROLE_ADMIN)
            .requestMatchers(HttpMethod.DELETE, orderPathWithId, orderStatusPathWithId, orderHistorySearchByOrderIdPath)
            .hasAuthority(ROLE_ADMIN)
            .requestMatchers(HttpMethod.PATCH, orderPathWithId, orderStatusPathWithId, orderHistorySearchByOrderIdPath)
            .hasAuthority(ROLE_ADMIN)
            .anyRequest()
            .authenticated()
        )
        .exceptionHandling(exceptions -> exceptions
            .accessDeniedHandler(accessDeniedHandler)     // Para el 403
            .authenticationEntryPoint(authenticationEntryPoint) // Para el 401
        )
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(resourceServer -> resourceServer
            .jwt(Customizer.withDefaults())
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler));
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