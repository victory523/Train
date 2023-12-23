package mucsi96.traininglog.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import io.github.mucsi96.kubetools.security.KubetoolsSecurityConfigurer;
import io.github.mucsi96.kubetools.security.MockSecurityConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

  @Bean
  @Profile("prod")
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      KubetoolsSecurityConfigurer kubetoolsSecurityConfigurer) throws Exception {
    return http
        .securityMatcher("/weight/**", "/ride/**")
        .with(kubetoolsSecurityConfigurer, Customizer.withDefaults())
        .build();
  }

  @Bean
  @Profile("!prod")
  SecurityFilterChain mockSecurityFilterChain(
      HttpSecurity http,
      MockSecurityConfigurer mockSecurityConfigurer) throws Exception {
    return http
        .securityMatcher("/weight/**", "/ride/**")
        .with(mockSecurityConfigurer, Customizer.withDefaults())
        .build();
  }

  @Bean
  public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(
      JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
    return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
  }
}
