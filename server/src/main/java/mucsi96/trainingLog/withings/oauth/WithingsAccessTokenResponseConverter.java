package mucsi96.trainingLog.withings.oauth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.core.TechnicalException;
import mucsi96.trainingLog.withings.data.GetAccessTokenResponseBody;
import mucsi96.trainingLog.withings.data.WithingsResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class WithingsAccessTokenResponseConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> source) {
        ObjectMapper mapper = new ObjectMapper();
        WithingsResponse<GetAccessTokenResponseBody> response =
                mapper.convertValue(source, new TypeReference<>() {});

        if (response.getError() != null) {
            log.error("Withings error {}", response.getError());
            throw new TechnicalException();
        }

        if (response.getStatus() != 0) {
            throw new TechnicalException();
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
}
