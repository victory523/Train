package mucsi96.trainingLog.oauth;

import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.withings.oauth.WithingsAccessTokenResponseClient;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final DefaultAuthorizationCodeTokenResponseClient defaultAuthorizationCodeTokenResponseClient;
    private final WithingsAccessTokenResponseClient withingsAccessTokenResponseClient;

    public AccessTokenResponseClient(WithingsAccessTokenResponseClient withingsAccessTokenResponseClient) {
        this.defaultAuthorizationCodeTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        this.withingsAccessTokenResponseClient = withingsAccessTokenResponseClient;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        String registrationId = authorizationGrantRequest.getClientRegistration().getRegistrationId();
        log.info("Requesting {} access token", registrationId);
        return getTokenResponseClient(registrationId).getTokenResponse(authorizationGrantRequest);
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> getTokenResponseClient(String registrationId) {
        if (WithingsClient.id.equals(registrationId)) {
            return withingsAccessTokenResponseClient;
        }

        return defaultAuthorizationCodeTokenResponseClient;
    }
}
