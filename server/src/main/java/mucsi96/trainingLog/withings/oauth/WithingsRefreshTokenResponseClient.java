package mucsi96.trainingLog.withings.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.*;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


@Slf4j
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
