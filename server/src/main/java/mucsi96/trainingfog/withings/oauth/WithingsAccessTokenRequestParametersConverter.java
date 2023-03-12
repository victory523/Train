package mucsi96.trainingfog.withings.oauth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class WithingsAccessTokenRequestParametersConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, MultiValueMap<String, String>> {

    @Override
    public MultiValueMap<String, String> convert(OAuth2AuthorizationCodeGrantRequest source) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("action", "requesttoken");
        parameters.add(OAuth2ParameterNames.CLIENT_ID, source.getClientRegistration().getClientId());
        parameters.add(OAuth2ParameterNames.CLIENT_SECRET, source.getClientRegistration().getClientSecret());
        return parameters;
    }
}
