package mucsi96.trainingfog.oauth;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

  private final OAuth2AuthorizedClientService authorizedClientService;

  public AuthorizedClientRepository(
    ClientRegistrationRepository clientRegistrationRepository,
    JdbcOperations jdbcOperations
  ) {
    this.authorizedClientService = new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
  }

  @Override
  public OAuth2AuthorizedClient loadAuthorizedClient(
    String clientRegistrationId,
    Authentication principal,
    HttpServletRequest request
  ) {
    return this.authorizedClientService.loadAuthorizedClient(clientRegistrationId, principal.getName());
  }

  @Override
  public void saveAuthorizedClient(
    OAuth2AuthorizedClient authorizedClient,
    Authentication principal,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    this.authorizedClientService.saveAuthorizedClient(authorizedClient, principal);

    if (authorizedClient.getClientRegistration().getRedirectUri().contains("/authorize/oauth2/")) {
      throw new RedirectToHomeException();
    }
  }

  @Override
  public void removeAuthorizedClient(
    String clientRegistrationId,
    Authentication principal,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    this.authorizedClientService.removeAuthorizedClient(clientRegistrationId, principal.getName());
  }
}
