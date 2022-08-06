package mucsi96.trainingLog.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthorizationFailureHandler implements OAuth2AuthorizationFailureHandler {

    @Override
    public void onAuthorizationFailure(OAuth2AuthorizationException authorizationException, Authentication principal, Map<String, Object> attributes) {
        HttpServletRequest request = (HttpServletRequest) attributes.get(HttpServletRequest.class.getName());

        if (request.getHeader(HttpHeaders.ACCEPT).equals(MediaType.APPLICATION_JSON_VALUE)) {
            throw new UnauthorizedException();
        }
    }
}
