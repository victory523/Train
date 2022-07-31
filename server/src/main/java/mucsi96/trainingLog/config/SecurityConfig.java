package mucsi96.trainingLog.config;

import mucsi96.trainingLog.withings.oauth.WithingsAccessTokenResponseClient;
import mucsi96.trainingLog.withings.oauth.WithingsAuthorizationFailureHandler;
import mucsi96.trainingLog.withings.oauth.WithingsRefreshTokenResponseClient;
import mucsi96.trainingLog.withings.oauth.WithingsUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${app.publicUrl}")
    String PUBLIC_URL;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            WithingsUserService userService,
            WithingsAccessTokenResponseClient accessTokenResponseClient
    ) throws Exception {
        http.oauth2Login()
                .defaultSuccessUrl(PUBLIC_URL);

        http.oauth2Login()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient);

        http.oauth2Login()
                .userInfoEndpoint()
                .userService(userService);

        http.oauth2Client()
                .authorizationCodeGrant()
                .accessTokenResponseClient(accessTokenResponseClient);

        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
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
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        authorizedClientManager.setAuthorizationFailureHandler(authorizationFailureHandler);

        return authorizedClientManager;
    }
}
