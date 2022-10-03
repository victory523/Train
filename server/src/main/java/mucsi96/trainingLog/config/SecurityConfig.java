package mucsi96.trainingLog.config;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.google.oauth.GoogleClient;
import mucsi96.trainingLog.oauth.*;
import org.springframework.beans.factory.annotation.Value;
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
  private final AuthorizedClientRepository authorizedClientRepository;
  private final RefreshTokenResponseClient refreshTokenResponseClient;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final OAuthAuthenticationSuccessHandler authenticationSuccessHandler;
  private final RedirectToHomeFilter redirectToHomeFilter;
  @Value("${management.server.port}")
  private String managementPort;

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    int managementPort = Integer.valueOf(this.managementPort);

    http.authorizeRequests().requestMatchers(
      request -> request.getLocalPort() == managementPort
    ).permitAll();

    http.authorizeRequests().anyRequest().authenticated();

    http.csrf().disable();

    http.headers().frameOptions().disable();

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
