package com.example.br.keycloakdemo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class KeycloakDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(KeycloakDemoApplication.class, args);
  }

  @Bean
  AuthoritiesConverter realmRolesAuthoritiesConverter() {
    return claims -> {
      var realmAccess = Optional.ofNullable(
          (Map<String, Object>) claims.get("realm_access"));
      var roles = realmAccess.flatMap(
          map -> Optional.ofNullable((List<String>) map.get("roles")));
      return roles.stream().flatMap(Collection::stream)
          .map(SimpleGrantedAuthority::new)
          .map(GrantedAuthority.class::cast)
          .toList();
    };
  }

  @Bean
  GrantedAuthoritiesMapper authenticationConverter(
      Converter<Map<String, Object>, Collection<GrantedAuthority>> authoritiesConverter) {
    return (authorities) -> authorities.stream()
        .filter(authority -> authority instanceof OidcUserAuthority)
        .map(OidcUserAuthority.class::cast)
        .map(OidcUserAuthority::getIdToken)
        .map(OidcIdToken::getClaims)
        .map(authoritiesConverter::convert)
        .filter(Objects::nonNull)
        .flatMap(roles -> roles.stream())
        .collect(Collectors.toSet());
  }

  @Bean
  SecurityFilterChain clientSecurityFilterChain(
      HttpSecurity http,
      ClientRegistrationRepository clientRegistrationRepository) throws Exception {
    http.oauth2Login(Customizer.withDefaults());
    http.logout((logout) -> {
      var logoutSuccessHandler =
          new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
      logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
      logout.logoutSuccessHandler(logoutSuccessHandler);
    });

    http.authorizeHttpRequests(requests -> {
      requests.requestMatchers("/", "/error", "/favicon.ico").permitAll();
    });

    return http.build();
  }

}
