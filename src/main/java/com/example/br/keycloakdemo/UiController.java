package com.example.br.keycloakdemo;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

  public Logger logger = LoggerFactory.getLogger(UiController.class);

  private final OAuth2AuthorizedClientService authorizedClientService;

  public UiController(OAuth2AuthorizedClientService authorizedClientService) {
    this.authorizedClientService = authorizedClientService;
  }

  @GetMapping("/")
  public String getIndex(Model model, Authentication auth) {

    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
    if (oauthToken != null) {
      OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
          oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
      String accessToken = client.getAccessToken().getTokenValue();
      model.addAttribute("accessToken", accessToken);
    }

    model.addAttribute("name",
        auth instanceof OAuth2AuthenticationToken oauth
            && oauth.getPrincipal() instanceof OidcUser oidc
            ? oidc.getPreferredUsername()
            : "");
    model.addAttribute("idToken",
        auth instanceof OAuth2AuthenticationToken oauth
            && oauth.getPrincipal() instanceof OidcUser oidc
            ? oidc.getIdToken().getTokenValue()
            : "");
    model.addAttribute("isAuthenticated",
        auth != null && auth.isAuthenticated());
    model.addAttribute("isNice",
        auth != null && auth.getAuthorities().stream().anyMatch(authority -> {
          return Objects.equals("NICE", authority.getAuthority());
        }));
    return "index.html";
  }

  @GetMapping("/nice")
  public String getNice(Model model, Authentication auth) {
    return "nice.html";
  }
}
