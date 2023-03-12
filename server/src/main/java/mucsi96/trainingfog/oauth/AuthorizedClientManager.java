package mucsi96.trainingfog.oauth;

import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;

public class AuthorizedClientManager implements OAuth2AuthorizedClientManager {
  private final DefaultOAuth2AuthorizedClientManager authorizedClientManager;

  public AuthorizedClientManager(
    ClientRegistrationRepository clientRegistrationRepository,
    AuthorizedClientRepository authorizedClientRepository,
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
