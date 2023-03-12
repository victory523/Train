package mucsi96.traininglog.withings.oauth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class WithingsRefreshTokenRequestParametersConverter implements Converter<OAuth2RefreshTokenGrantRequest, MultiValueMap<String, String>> {
    @Override
    public MultiValueMap<String, String> convert(OAuth2RefreshTokenGrantRequest source) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("action", "requesttoken");
        parameters.add(OAuth2ParameterNames.CLIENT_ID, source.getClientRegistration().getClientId());
        parameters.add(OAuth2ParameterNames.CLIENT_SECRET, source.getClientRegistration().getClientSecret());
        return parameters;
    }
}
