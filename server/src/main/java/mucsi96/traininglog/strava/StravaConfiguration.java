package mucsi96.traininglog.strava;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.RemoveAuthorizedClientOAuth2AuthorizationFailureHandler;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.github.mucsi96.kubetools.security.KubetoolsSecurityConfigurer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Configuration
@ConfigurationProperties(prefix = "strava")
@Slf4j
public class StravaConfiguration {
  public static final String registrationId = "strava-client";
  private String username;
  private String password;
  private String apiUri;

  @Bean
  SecurityFilterChain stravaSecurityFilterChain(
      HttpSecurity http,
      KubetoolsSecurityConfigurer kubetoolsSecurityConfigurer) throws Exception {
    return kubetoolsSecurityConfigurer.configure(http)
        .securityMatcher("/strava/**")
        .oauth2Client(configurer -> configurer
            .authorizationCodeGrant(customizer -> customizer
                .accessTokenResponseClient(stravaAccessTokenResponseClient())))
        .build();
  }

  @Bean
  OAuth2AuthorizedClientManager stravaAuthorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {

    DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
        clientRegistrationRepository, authorizedClientRepository);

    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken(configurer -> configurer.accessTokenResponseClient(stravaRefreshTokenResponseClient()))
        .build();

    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    Set<String> removeAuthorizedClientErrorCodes = Set.of(
        OAuth2ErrorCodes.INVALID_GRANT,
        OAuth2ErrorCodes.INVALID_TOKEN, "invalid_token_response");

    authorizedClientManager.setAuthorizationFailureHandler(new RemoveAuthorizedClientOAuth2AuthorizationFailureHandler(
        (clientRegistrationId, principal, attributes) -> authorizedClientRepository.removeAuthorizedClient(
            clientRegistrationId, principal,
            (HttpServletRequest) attributes.get(HttpServletRequest.class.getName()),
            (HttpServletResponse) attributes.get(HttpServletResponse.class.getName())),
        removeAuthorizedClientErrorCodes));

    return authorizedClientManager;
  }

  OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> stravaAccessTokenResponseClient() {
    OAuth2AuthorizationCodeGrantRequestEntityConverter requestEntityConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
    requestEntityConverter.addParametersConverter(stravaAccessTokenRequestParametersConverter());

    DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
    accessTokenResponseClient.setRequestEntityConverter(requestEntityConverter);
    return accessTokenResponseClient;
  }

  OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> stravaRefreshTokenResponseClient() {
    OAuth2RefreshTokenGrantRequestEntityConverter requestEntityConverter = new OAuth2RefreshTokenGrantRequestEntityConverter();
    requestEntityConverter.addParametersConverter(stravaRefreshTokenRequestParametersConverter());

    DefaultRefreshTokenTokenResponseClient tokenResponseClient = new DefaultRefreshTokenTokenResponseClient();
    tokenResponseClient.setRequestEntityConverter(requestEntityConverter);
    return tokenResponseClient;
  }

  Converter<OAuth2AuthorizationCodeGrantRequest, MultiValueMap<String, String>> stravaAccessTokenRequestParametersConverter() {
    return request -> {
      log.info("Requesting new Strava access token");
      MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
      parameters.add(OAuth2ParameterNames.CLIENT_ID, request.getClientRegistration().getClientId());
      parameters.add(OAuth2ParameterNames.CLIENT_SECRET, request.getClientRegistration().getClientSecret());
      // parameters.add(OAuth2ParameterNames.CODE,
      //     request.getAuthorizationExchange().getAuthorizationResponse().getCode());
      // parameters.add(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");
      return parameters;
    };
  }

  Converter<OAuth2RefreshTokenGrantRequest, MultiValueMap<String, String>> stravaRefreshTokenRequestParametersConverter() {
    return request -> {
      log.info("Refreshing expired Strava access token");
      MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
      parameters.add(OAuth2ParameterNames.CLIENT_ID, request.getClientRegistration().getClientId());
      parameters.add(OAuth2ParameterNames.CLIENT_SECRET, request.getClientRegistration().getClientSecret());
      // parameters.add(OAuth2ParameterNames.GRANT_TYPE, "refresh_token");
      // parameters.add(OAuth2ParameterNames.REFRESH_TOKEN, request.getRefreshToken().getTokenValue());
      return parameters;
    };
  }

}
