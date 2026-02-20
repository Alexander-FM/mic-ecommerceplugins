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

  private final CustomAccessDeniedHandler accessDeniedHandler;

  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler,
    CustomAuthenticationEntryPoint authenticationEntryPoint) {
    this.accessDeniedHandler = accessDeniedHandler;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    // Creamos un array con todas las rutas protegidas para no repetir código
    final String[] allMaintenancePaths = {
      customerPath + "/**", employeePath + "/**", rolePath + "/**",
      userPath + "/**", brandPath + "/**", categoryPath + "/**",
      productPath + "/**", productImagePath + "/**", googleDrivePath + "/**"
    };
    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
        //1. Rutas públicas (sin autenticación) para login y consultas de datos activos
        .requestMatchers("/api/maintenance/users/authorized", "/api/maintenance/users/login", "/api/maintenance/products/active",
          "/api/maintenance/brands/active", "/api/maintenance/categories/active").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/maintenance/roles").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/maintenance/customers").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/maintenance/users").permitAll()
        // 2. Operaciones permitidas para ADMIN y USER (GET y POST)
        .requestMatchers(HttpMethod.GET, allMaintenancePaths).hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
        .requestMatchers(HttpMethod.POST, allMaintenancePaths).hasAnyAuthority(ROLE_ADMIN, ROLE_USER)
        // 3. Operaciones exclusivas para ADMIN (PUT, PATCH, DELETE)
        .requestMatchers(HttpMethod.PUT, allMaintenancePaths).hasAuthority(ROLE_ADMIN)
        .requestMatchers(HttpMethod.PATCH, allMaintenancePaths).hasAuthority(ROLE_ADMIN)
        .requestMatchers(HttpMethod.DELETE, allMaintenancePaths).hasAuthority(ROLE_ADMIN)
        .anyRequest().authenticated()
      )
      .exceptionHandling(exceptions -> exceptions
        .accessDeniedHandler(accessDeniedHandler)     // Para el 403
        .authenticationEntryPoint(authenticationEntryPoint) // Para el 401
      )
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      //.oauth2Login(oauth2 -> oauth2.loginPage("/oauth2/authorization/maintenance-client"))
      //.oauth2Client(Customizer.withDefaults())
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