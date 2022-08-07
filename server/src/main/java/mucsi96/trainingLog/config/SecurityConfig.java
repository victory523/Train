package mucsi96.trainingLog.config;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.oauth.*;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final WebConfig webConfig;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            UserService userService,
            AccessTokenResponseClient accessTokenResponseClient
    ) throws Exception {
        http.oauth2Login()
                .defaultSuccessUrl(webConfig.getPublicAppUrl());

        http.oauth2Login()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient);

        http.oauth2Login()
                .userInfoEndpoint()
                .userService(userService);

        return http.build();
    }

    @Bean OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            CookieBasedAuthorizedClientRepository authorizedClientRepository,
            RefreshTokenResponseClient refreshTokenResponseClient
    ) {
        return new AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientRepository,
                refreshTokenResponseClient

        );
    }
}
