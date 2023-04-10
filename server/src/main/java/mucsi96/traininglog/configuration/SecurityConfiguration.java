package mucsi96.traininglog.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;

import io.github.mucsi96.kubetools.security.KubetoolsSecurityConfigurer;
import mucsi96.traininglog.core.RedirectToHomeRequestCache;
import mucsi96.traininglog.oauth.AccessTokenResponseClient;
import mucsi96.traininglog.oauth.AuthorizedClientManager;
import mucsi96.traininglog.oauth.RefreshTokenResponseClient;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      KubetoolsSecurityConfigurer kubetoolsSecurityConfigurer,
      AccessTokenResponseClient accessTokenResponseClient) throws Exception {
    return kubetoolsSecurityConfigurer.configure(http)
        .requestCache(configurer -> configurer.requestCache(new RedirectToHomeRequestCache()))
        .oauth2Client(configurer -> configurer
            .authorizationCodeGrant(customizer -> customizer
                .accessTokenResponseClient(accessTokenResponseClient)))
        .build();
  }

  @Bean
  public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(
      JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
    return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
  }

  @Bean
  OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository,
      RefreshTokenResponseClient refreshTokenResponseClient) {
    return new AuthorizedClientManager(
        clientRegistrationRepository,
        authorizedClientRepository,
        refreshTokenResponseClient);
  }
}
