package mucsi96.trainingLog.oauth;

import mucsi96.trainingLog.withings.oauth.WithingsClient;
import mucsi96.trainingLog.withings.oauth.WithingsUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final WithingsUserService withingsUserService;

    public UserService(WithingsUserService withingsUserService) {
        this.defaultOAuth2UserService = new DefaultOAuth2UserService();
        this.withingsUserService = withingsUserService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return getUserService(
                userRequest.getClientRegistration().getRegistrationId()
        ).loadUser(userRequest);
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> getUserService(String registrationId) {
        if (WithingsClient.id.equals(registrationId)) {
            return withingsUserService;
        }

        return defaultOAuth2UserService;
    }
}
