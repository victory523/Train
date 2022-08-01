package mucsi96.trainingLog.config;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.withings.oauth.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final WebConfig webConfig;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            WithingsUserService userService,
            WithingsAccessTokenResponseClient accessTokenResponseClient
    ) throws Exception {
        http.oauth2Login()
                .defaultSuccessUrl(webConfig.getPublicUrl());

        http.oauth2Login()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient);

        http.oauth2Login()
                .userInfoEndpoint()
                .userService(userService);

        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            CookieBasedAuthorizedClientRepository cookieBasedAuthorizedClientRepository,
            WithingsAuthorizationFailureHandler authorizationFailureHandler,
            WithingsRefreshTokenResponseClient withingsRefreshTokenResponseClient) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken(configurer -> {
                            configurer.accessTokenResponseClient(withingsRefreshTokenResponseClient);
                        })
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, cookieBasedAuthorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        authorizedClientManager.setAuthorizationFailureHandler(authorizationFailureHandler);

        return authorizedClientManager;
    }
}
