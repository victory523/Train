package mucsi96.traininglog.oauth;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

public class AuthorizedClientManager implements OAuth2AuthorizedClientManager {
  private final DefaultOAuth2AuthorizedClientManager authorizedClientManager;

  public AuthorizedClientManager(
    ClientRegistrationRepository clientRegistrationRepository,
    OAuth2AuthorizedClientRepository authorizedClientRepository,
    RefreshTokenResponseClient refreshTokenResponseClient
  ) {
    OAuth2AuthorizedClientProvider authorizedClientProvider =
      OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken(configurer -> configurer.accessTokenResponseClient(refreshTokenResponseClient))
        .build();

    this.authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
      clientRegistrationRepository,
      authorizedClientRepository
    );
    this.authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
  }

  @Override
  public OAuth2AuthorizedClient authorize(OAuth2AuthorizeRequest authorizeRequest) {
    return authorizedClientManager.authorize(authorizeRequest);
  }
}
