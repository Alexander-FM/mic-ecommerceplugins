package com.codecorecix.ecommerce.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.codecorecix.ecommerce.api.dto.response.EcommerceUserDetails;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${app.frontend.url}")
  private String frontendUrl;

  private static final String LOGIN_URL = "/login";

  private static final String WRITE_SCOPE = "write";

  private static final String READ_SCOPE = "read";

  private static final String INTERNAL_WRITE_SCOPE = "internal.write";

  private final Environment environment;

  private final UserDetailsService userDetailsService;

  public SecurityConfig(Environment environment, UserDetailsService userDetailsService) {
    this.environment = environment;
    this.userDetailsService = userDetailsService;

  }

  @Bean
  private static BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(final HttpSecurity http)
      throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    http
        .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .oidc(withDefaults());
    http
        .exceptionHandling(exceptions -> exceptions
            .defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint(LOGIN_URL),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
            ))
        .oauth2ResourceServer(resources -> resources.jwt(Customizer.withDefaults()));
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(final HttpSecurity http)
      throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(LOGIN_URL, "/api/auth/register", "/css/**", "/images/**", "/js/**", "/error", "/favicon.ico")
            .permitAll()
            .anyRequest()
            .authenticated())
        .formLogin(form -> form
            .loginPage(LOGIN_URL)
            .permitAll())
        .logout(logout -> logout
            .logoutSuccessUrl(frontendUrl + LOGIN_URL)
            .permitAll())
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Autowired
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    // Cliente para la comunicación SERVICIO-A-SERVICIO (Auth -> Maintenance)
    RegisteredClient maintenanceInternalClient = RegisteredClient
        .withId(UUID
            .randomUUID()
            .toString())
        .clientId("maintenance-client") // El ID que usará Auth para identificarse
        .clientSecret(passwordEncoder().encode("12345")) // El secreto
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // <-- ¡SOLO ESTE GRANT TYPE!
        .tokenSettings(TokenSettings
            .builder()
            .accessTokenTimeToLive(Duration.ofMinutes(30))
            .build())
        .scope(INTERNAL_WRITE_SCOPE) // <-- El permiso especial para operaciones internas
        .build();

    // Cliente para tu aplicación Angular (SPA)
    RegisteredClient spaClient = RegisteredClient
        .withId(UUID
            .randomUUID()
            .toString())
        .clientId("ecommerce-spa")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri(environment.getProperty("SPA_REDIRECT_URI", "http://localhost:4200/auth/callback"))
        .postLogoutRedirectUri(environment.getProperty("SPA_REDIRECT_URI", "http://localhost:4200/auth/callback"))
        .tokenSettings(TokenSettings
            .builder()
            .accessTokenTimeToLive(Duration.ofHours(1))
            .build())
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope(READ_SCOPE)
        .scope(WRITE_SCOPE)
        .clientSettings(ClientSettings
            .builder()
            .requireAuthorizationConsent(false)
            .requireProofKey(true)
            .build())
        .build();

    return new InMemoryRegisteredClientRepository(maintenanceInternalClient, spaClient);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID
            .randomUUID()
            .toString())
        .build();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return new ImmutableJWKSet<>(jwkSet);
  }

  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }

  @Bean
  public JwtDecoder jwtDecoder(final JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings
        .builder()
        .build();
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
    return context -> {
      if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
        Authentication authentication = context.getPrincipal();
        // --- ¡LA CORRECCIÓN ESTÁ AQUÍ! ---
        // Verificamos si el principal es un usuario humano antes de hacer el cast
        if (authentication.getPrincipal() instanceof EcommerceUserDetails user) {
          // Si es un usuario, añadimos sus claims específicos
          Set<String> authorities = user.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toSet());
          context.getClaims().claim("roles", authorities);

          if (user.getCustomerId() != null) {
            context.getClaims().claim("customerId", user.getCustomerId());
          }
          if (user.getEmployeeId() != null) {
            context.getClaims().claim("employeeId", user.getEmployeeId());
          }
          if (user.getCustomerName() != null && !user.getCustomerName().isBlank()) {
            context.getClaims().claim("displayName", user.getCustomerName());
          } else if (user.getEmployeeName() != null) {
            context.getClaims().claim("displayName", user.getEmployeeName());
          }
          context.getClaims().claim("username", user.getUsername());
        } else {
          // Es un token de client_credentials, añadimos los scopes como roles
          // no hacemos nada y dejamos que el token se genere con los claims por defecto (como el scope).
          Set<String> scopes = context.getRegisteredClient().getScopes();
          context.getClaims().claim("roles", scopes.stream().map(s -> "SCOPE_" + s).collect(Collectors.toSet()));
        }
      }
    };
  }
}