package mucsi96.trainingLog.config;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.google.oauth.GoogleClient;
import mucsi96.trainingLog.oauth.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final AccessTokenResponseClient accessTokenResponseClient;
  private final ClientRegistrationRepository clientRegistrationRepository;
  private final CookieBasedAuthorizedClientRepository authorizedClientRepository;
  private final RefreshTokenResponseClient refreshTokenResponseClient;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final OAuthAuthenticationSuccessHandler authenticationSuccessHandler;
  private final UnauthorizedClientFilter unauthorizedClientFilter;
  private final RedirectToHomeFilter redirectToHomeFilter;

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().authenticated();

    http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);

    http.oauth2Login()
      .loginPage(
        OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + GoogleClient.id
      );

    http.oauth2Login()
        .successHandler(authenticationSuccessHandler);

    http.oauth2Client()
      .authorizationCodeGrant()
      .accessTokenResponseClient(accessTokenResponseClient);

    http.addFilterBefore(redirectToHomeFilter, OAuth2AuthorizationCodeGrantFilter.class);
    http.addFilterBefore(unauthorizedClientFilter, OAuth2AuthorizationRequestRedirectFilter.class);

    return http.build();
  }

  @Bean
  OAuth2AuthorizedClientManager authorizedClientManager() {
    return new AuthorizedClientManager(
      clientRegistrationRepository,
      authorizedClientRepository,
      refreshTokenResponseClient
    );
  }
}
