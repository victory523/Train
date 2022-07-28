package mucsi96.trainingLog.config;

import mucsi96.trainingLog.withings.WithingsAuthentication;
import mucsi96.trainingLog.withings.oauth.WithingsAccessTokenResponseClient;
import mucsi96.trainingLog.withings.oauth.WithingsUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            WithingsUserService userService,
            WithingsAccessTokenResponseClient accessTokenResponseClient
    ) throws Exception {
        http.authorizeRequests()
                .antMatchers("/withings/weight").authenticated()
                .antMatchers("/login").permitAll()
                .antMatchers("/login/oauth2/**").permitAll();

        http.oauth2Login()
                .loginPage("/login")
                .defaultSuccessUrl("/login_success");

        http.oauth2Login()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient);

        http.oauth2Login()
                .userInfoEndpoint()
                .userService(userService);

        http.oauth2Client();

        return http.build();
    }

    @Bean
    @RequestScope
    public WithingsAuthentication withingsAuthentication(OAuth2AuthorizedClientService clientService) {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient("withings-client", authentication.getName());

            String accessToken = null;

            if (authorizedClient != null) {
                accessToken = authorizedClient.getAccessToken().getTokenValue();
            }

            return accessToken;
        };
    }
}
