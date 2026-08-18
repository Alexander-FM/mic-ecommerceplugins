package com.codecorecix.ecommerce.utils;

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
  @Value("${app.endpoints.customer}")
  private String customerPath;

  @Value("${app.endpoints.employee}")
  private String employeePath;

  @Value("${app.endpoints.role}")
  private String rolePath;

  @Value("${app.endpoints.user}")
  private String userPath;

  @Value("${app.endpoints.brand}")
  private String brandPath;

  @Value("${app.endpoints.category}")
  private String categoryPath;

  @Value("${app.endpoints.product}")
  private String productPath;

  @Value("${app.endpoints.product-image}")
  private String productImagePath;

  @Value("${app.endpoints.google-drive}")
  private String googleDrivePath;

  private static final String ROLE_ADMIN = "ROLE_ADMIN";

  private static final String ROLE_USER = "ROLE_USER";

  private static final String INTERNAL_WRITE = "internal.write";

  private final CustomAccessDeniedHandler accessDeniedHandler;

  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler,
                        CustomAuthenticationEntryPoint authenticationEntryPoint) {
    this.accessDeniedHandler = accessDeniedHandler;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    final String[] allMaintenancePaths = {
        customerPath + "/**", employeePath + "/**", rolePath + "/**",
        userPath + "/**", brandPath + "/**", categoryPath + "/**",
        productPath + "/**", productImagePath + "/**", googleDrivePath + "/**"
    };

    http
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            // 1. Rutas públicas (sin autenticación)
            .requestMatchers("/api/maintenance/users/login",
                "/api/maintenance/customers/username/{username}", "/api/maintenance/employees/username/{username}",
                "/api/maintenance/products/active", "/api/maintenance/products/{id}",
                "/api/maintenance/brands/active", "/api/maintenance/categories/active",
                "/api/maintenance/products/category/{categoryId}")
            .permitAll()

            // 2. Operaciones internas para comunicación servicio-a-servicio
            .requestMatchers(HttpMethod.GET, "/api/maintenance/roles/internal/**")
            .hasAuthority(INTERNAL_WRITE)
            .requestMatchers(HttpMethod.DELETE, "/api/maintenance/users/internal/**")
            .hasAuthority(INTERNAL_WRITE)
            .requestMatchers(HttpMethod.POST, "/api/maintenance/customers")
            .hasAuthority(INTERNAL_WRITE)
            .requestMatchers(HttpMethod.POST, "/api/maintenance/users")
            .hasAuthority(INTERNAL_WRITE)

            // 3. Reglas específicas para usuarios (DEBEN IR ANTES DE LAS REGLAS GENERALES DE ADMIN)
            .requestMatchers(HttpMethod.PUT, customerPath + "/{id}")
            .hasAnyAuthority(ROLE_USER, ROLE_ADMIN)

            // 4. Operaciones de lectura para ambos roles
            .requestMatchers(HttpMethod.GET, allMaintenancePaths)
            .hasAnyAuthority(ROLE_ADMIN, ROLE_USER)

            // 5. Operaciones de escritura generales (POST)
            .requestMatchers(HttpMethod.POST, allMaintenancePaths)
            .hasAnyAuthority(ROLE_ADMIN, ROLE_USER)

            // 6. Operaciones de modificación exclusivas para ADMIN (las más generales al final)
            .requestMatchers(HttpMethod.PUT, allMaintenancePaths)
            .hasAuthority(ROLE_ADMIN)
            .requestMatchers(HttpMethod.PATCH, allMaintenancePaths)
            .hasAuthority(ROLE_ADMIN)
            .requestMatchers(HttpMethod.DELETE, allMaintenancePaths)
            .hasAuthority(ROLE_ADMIN)

            .anyRequest()
            .authenticated()
        )
        .exceptionHandling(exceptions -> exceptions
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(authenticationEntryPoint)
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
    grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
    grantedAuthoritiesConverter.setAuthorityPrefix("");
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}