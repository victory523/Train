package mucsi96.trainingLog.oauth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;

import javax.servlet.http.HttpServletRequest;

public class AuthorizedClientManager implements OAuth2AuthorizedClientManager {
    private final DefaultOAuth2AuthorizedClientManager authorizedClientManager;

    public AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            CookieBasedAuthorizedClientRepository authorizedClientRepository,
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
        try {
            return authorizedClientManager.authorize(authorizeRequest);
        } catch (OAuth2AuthorizationException ex) {
            HttpServletRequest request = authorizeRequest.getAttribute(HttpServletRequest.class.getName());

            if (MediaType.APPLICATION_JSON_VALUE.equals(request.getHeader(HttpHeaders.ACCEPT))) {
                throw new UnauthorizedException();
            }

            throw ex;
        }
    }
}
