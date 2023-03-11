package mucsi96.trainingLog.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.core.FilterChainExceptionHandler;
import mucsi96.trainingLog.oauth.AccessTokenResponseClient;
import mucsi96.trainingLog.oauth.AuthorizedClientManager;
import mucsi96.trainingLog.oauth.AuthorizedClientRepository;
import mucsi96.trainingLog.oauth.RedirectToHomeFilter;
import mucsi96.trainingLog.oauth.RefreshTokenResponseClient;
import mucsi96.trainingLog.security.AutheliaHeaderAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Value("${management.server.port}")
  private String managementPort;

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(
      HttpSecurity http,
      AccessTokenResponseClient accessTokenResponseClient,
      RedirectToHomeFilter redirectToHomeFilter,
      AutheliaHeaderAuthenticationFilter autheliaHeaderAuthenticationFilter,
      FilterChainExceptionHandler filterChainExceptionHandler)
      throws Exception {

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.anonymous().disable();
    http.csrf().disable();
    http.headers().frameOptions().disable();
    http.formLogin().disable();
    http.logout().disable();

    http.oauth2Client()
        .authorizationCodeGrant()
        .accessTokenResponseClient(accessTokenResponseClient);

    http.addFilterBefore(redirectToHomeFilter, OAuth2AuthorizationCodeGrantFilter.class);
    http.addFilterAfter(autheliaHeaderAuthenticationFilter, HeaderWriterFilter.class);
    http.addFilterBefore(filterChainExceptionHandler, AutheliaHeaderAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      AuthorizedClientRepository authorizedClientRepository,
      RefreshTokenResponseClient refreshTokenResponseClient) {
    return new AuthorizedClientManager(
        clientRegistrationRepository,
        authorizedClientRepository,
        refreshTokenResponseClient);
  }
}
