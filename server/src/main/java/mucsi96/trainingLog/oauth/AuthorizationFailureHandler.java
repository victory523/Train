package mucsi96.trainingLog.oauth;

import mucsi96.trainingLog.withings.WithingsUnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthorizationFailureHandler implements OAuth2AuthorizationFailureHandler {
    @Override
    public void onAuthorizationFailure(OAuth2AuthorizationException authorizationException, Authentication principal, Map<String, Object> attributes) {
        throw new WithingsUnauthorizedException();
    }
}
