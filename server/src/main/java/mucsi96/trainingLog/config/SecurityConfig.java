package mucsi96.trainingLog.config;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.oauth.*;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AccessTokenResponseClient accessTokenResponseClient;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CookieBasedAuthorizedClientRepository authorizedClientRepository;
    private final RefreshTokenResponseClient refreshTokenResponseClient;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2Login()
                .loginPage("/login");

        http.oauth2Login()
                .defaultSuccessUrl("/");

        http.oauth2Client()
                .authorizationCodeGrant()
                .accessTokenResponseClient(accessTokenResponseClient);

        return http.build();
    }

    @Bean OAuth2AuthorizedClientManager authorizedClientManager() {
        return new AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientRepository,
                refreshTokenResponseClient
        );
    }
}
