package mucsi96.traininglog.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.web.SecurityFilterChain;

import io.github.mucsi96.kubetools.security.KubetoolsSecurityConfigurer;
import mucsi96.traininglog.oauth.AccessTokenResponseClient;
import mucsi96.traininglog.oauth.AuthorizedClientManager;
import mucsi96.traininglog.oauth.AuthorizedClientRepository;
import mucsi96.traininglog.oauth.RedirectToHomeFilter;
import mucsi96.traininglog.oauth.RefreshTokenResponseClient;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      KubetoolsSecurityConfigurer kubetoolsSecurityConfigurer,
      AccessTokenResponseClient accessTokenResponseClient,
      RedirectToHomeFilter redirectToHomeFilter) throws Exception {
    return kubetoolsSecurityConfigurer.configure(http)
        .oauth2Client(configurer -> configurer
            .authorizationCodeGrant(customizer -> customizer.accessTokenResponseClient(accessTokenResponseClient)))
        .addFilterBefore(redirectToHomeFilter, OAuth2AuthorizationCodeGrantFilter.class)
        .build();
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
