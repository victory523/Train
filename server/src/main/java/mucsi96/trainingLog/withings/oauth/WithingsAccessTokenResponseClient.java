package mucsi96.trainingLog.withings.oauth;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class WithingsAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    DefaultAuthorizationCodeTokenResponseClient tokenResponseClient;

    public WithingsAccessTokenResponseClient(WithingsAccessTokenResponseConverter withingsAccessTokenResponseConverter) {
        OAuth2AuthorizationCodeGrantRequestEntityConverter requestEntityConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        requestEntityConverter.addParametersConverter(new WithingsAccessTokenRequestParametersConverter());

        OAuth2AccessTokenResponseHttpMessageConverter accessTokenResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        accessTokenResponseConverter.setAccessTokenResponseConverter(withingsAccessTokenResponseConverter);

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(),
                accessTokenResponseConverter));

        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        this.tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        this.tokenResponseClient.setRequestEntityConverter(requestEntityConverter);
        this.tokenResponseClient.setRestOperations(restTemplate);
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        return this.tokenResponseClient.getTokenResponse(authorizationGrantRequest);
    }
}
