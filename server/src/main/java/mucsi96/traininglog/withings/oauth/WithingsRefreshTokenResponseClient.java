package mucsi96.traininglog.withings.oauth;

import java.util.Arrays;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WithingsRefreshTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> {

    private final DefaultRefreshTokenTokenResponseClient tokenResponseClient;

    public WithingsRefreshTokenResponseClient(
            WithingsAccessTokenResponseConverter withingsAccessTokenResponseConverter
    ) {
        OAuth2RefreshTokenGrantRequestEntityConverter requestEntityConverter = new OAuth2RefreshTokenGrantRequestEntityConverter();
        requestEntityConverter.addParametersConverter(new WithingsRefreshTokenRequestParametersConverter());

        OAuth2AccessTokenResponseHttpMessageConverter accessTokenResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        accessTokenResponseConverter.setAccessTokenResponseConverter(withingsAccessTokenResponseConverter);

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(),
                accessTokenResponseConverter));

        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        this.tokenResponseClient = new DefaultRefreshTokenTokenResponseClient();
        this.tokenResponseClient.setRequestEntityConverter(requestEntityConverter);
        this.tokenResponseClient.setRestOperations(restTemplate);
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2RefreshTokenGrantRequest authorizationGrantRequest) {
        return tokenResponseClient.getTokenResponse(authorizationGrantRequest);
    }
}
