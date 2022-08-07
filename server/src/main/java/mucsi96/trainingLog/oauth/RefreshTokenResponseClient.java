package mucsi96.trainingLog.oauth;

import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import mucsi96.trainingLog.withings.oauth.WithingsRefreshTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefreshTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> {

    private final DefaultRefreshTokenTokenResponseClient defaultRefreshTokenTokenResponseClient;
    private final WithingsRefreshTokenResponseClient withingsRefreshTokenResponseClient;

    public RefreshTokenResponseClient(WithingsRefreshTokenResponseClient withingsRefreshTokenResponseClient) {
        this.defaultRefreshTokenTokenResponseClient = new DefaultRefreshTokenTokenResponseClient();
        this.withingsRefreshTokenResponseClient = withingsRefreshTokenResponseClient;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2RefreshTokenGrantRequest authorizationGrantRequest) {
        String registrationId = authorizationGrantRequest.getClientRegistration().getRegistrationId();
        log.info("Refreshing {} access token", registrationId);
        return getTokenResponseClient(registrationId).getTokenResponse(authorizationGrantRequest);
    }

    private OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> getTokenResponseClient(String registrationId) {
        if (WithingsClient.id.equals(registrationId)) {
            return withingsRefreshTokenResponseClient;
        }

        return defaultRefreshTokenTokenResponseClient;
    }
}
