package mucsi96.traininglog.withings.oauth;

import java.util.Arrays;

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

@Component
public class WithingsAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final DefaultAuthorizationCodeTokenResponseClient tokenResponseClient;

    public WithingsAccessTokenResponseClient(
            WithingsAccessTokenRequestParametersConverter withingsAccessTokenRequestParametersConverter,
            WithingsAccessTokenResponseConverter withingsAccessTokenResponseConverter

    ) {
        OAuth2AuthorizationCodeGrantRequestEntityConverter requestEntityConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        requestEntityConverter.addParametersConverter(withingsAccessTokenRequestParametersConverter);

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
