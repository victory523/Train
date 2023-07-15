package mucsi96.traininglog.withings.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;

import io.github.mucsi96.kubetools.security.KubetoolsSecurityConfigurer;

@Configuration
public class WithingsOAuthConfiguration {

  @Bean
  SecurityFilterChain withingsSecurityFilterChain(
      HttpSecurity http,
      KubetoolsSecurityConfigurer kubetoolsSecurityConfigurer,
      WithingsAccessTokenResponseClient accessTokenResponseClient) throws Exception {
    return kubetoolsSecurityConfigurer.configure(http)
        .securityMatcher("/withings/authorize")
        .oauth2Client(configurer -> configurer
            .authorizationCodeGrant(customizer -> customizer
                .accessTokenResponseClient(accessTokenResponseClient)))
        .build();
  }

  @Bean
  OAuth2AuthorizedClientManager withingsAuthorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository,
      WithingsRefreshTokenResponseClient refreshTokenResponseClient) {

    DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
        clientRegistrationRepository, authorizedClientRepository);

    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken(configurer -> configurer.accessTokenResponseClient(refreshTokenResponseClient))
        .build();

    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }
}
