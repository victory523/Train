package mucsi96.trainingLog.withings.oauth;

import mucsi96.trainingLog.withings.WithingsTechnicalException;
import mucsi96.trainingLog.withings.data.GetAccessTokenResponseBody;
import mucsi96.trainingLog.withings.data.WithingsResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class WithingsAccessTokenResponseConverter extends AbstractHttpMessageConverter<OAuth2AccessTokenResponse> {

    private static final ParameterizedTypeReference<WithingsResponse<GetAccessTokenResponseBody>> RESPONSE =
            new ParameterizedTypeReference<WithingsResponse<GetAccessTokenResponseBody>>() {
    };
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

    public WithingsAccessTokenResponseConverter(){
        super(StandardCharsets.UTF_8, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return OAuth2AccessTokenResponse.class.isAssignableFrom(clazz);
    }

    @Override
    protected OAuth2AccessTokenResponse readInternal(Class<? extends OAuth2AccessTokenResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        WithingsResponse<GetAccessTokenResponseBody> response =
                (WithingsResponse<GetAccessTokenResponseBody>) jackson2HttpMessageConverter.read(RESPONSE.getType(), null, inputMessage);

        if (response.getStatus() != 0) {
            throw new WithingsTechnicalException();
        }

        GetAccessTokenResponseBody body = response.getBody();

        return OAuth2AccessTokenResponse
                .withToken(body.getAccessToken())
                .refreshToken(body.getRefreshToken())
                .expiresIn(body.getExpiresIn())
                .scopes(Collections.singleton(body.getScope()))
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .additionalParameters(Collections.singletonMap("userId", body.getUserId()))
                .build();
    }

    @Override
    protected void writeInternal(OAuth2AccessTokenResponse oAuth2AccessTokenResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    }
}
