package mucsi96.traininglog.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import io.github.mucsi96.kubetools.core.FilterChainExceptionHandlerFilter;
import io.github.mucsi96.kubetools.security.AutheliaHeaderAuthenticationFilter;
import mucsi96.traininglog.oauth.AccessTokenResponseClient;
import mucsi96.traininglog.oauth.AuthorizedClientManager;
import mucsi96.traininglog.oauth.AuthorizedClientRepository;
import mucsi96.traininglog.oauth.RedirectToHomeFilter;
import mucsi96.traininglog.oauth.RefreshTokenResponseClient;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
      AccessTokenResponseClient accessTokenResponseClient,
      RedirectToHomeFilter redirectToHomeFilter,
      AuthenticationManager authenticationManager)
      throws Exception {

    return http
        .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .anonymous(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .headers(configurer -> configurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .oauth2Client(configurer -> configurer
            .authorizationCodeGrant(customizer -> customizer.accessTokenResponseClient(accessTokenResponseClient)))
        .addFilter(new AutheliaHeaderAuthenticationFilter(authenticationManager))
        .addFilterBefore(new FilterChainExceptionHandlerFilter(resolver),
            AbstractPreAuthenticatedProcessingFilter.class)
        .addFilterBefore(redirectToHomeFilter, OAuth2AuthorizationCodeGrantFilter.class)
        .build();
  }

  @Bean
  OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      AuthorizedClientRepository authorizedClientRepository,
      RefreshTokenResponseClient refreshTokenResponseClient) {
    return new AuthorizedClientManager(
        clientRegistrationRepository,
        authorizedClientRepository,
        refreshTokenResponseClient);
  }
}
