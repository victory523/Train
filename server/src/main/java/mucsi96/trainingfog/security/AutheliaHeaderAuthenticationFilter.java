package mucsi96.trainingfog.security;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AutheliaHeaderAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  public AutheliaHeaderAuthenticationFilter() {
    this.setAuthenticationManager(authentication -> {

      if (authentication.getCredentials() == null) {
        throw new PreAuthenticatedCredentialsNotFoundException(
            "Authelia headers not found in request.");
      }

      return new PreAuthenticatedAuthenticationToken(
          authentication.getPrincipal(), authentication.getCredentials(),
          authentication.getAuthorities());
    });

    this.setContinueFilterChainOnUnsuccessfulAuthentication(false);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    AutheliaUser user = getAutheliaUser(request);

    if (user == null) {
      return "N/A";
    }

    return user;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return getAutheliaUser(request);
  }

  private AutheliaUser getAutheliaUser(HttpServletRequest request) {
    String username = request.getHeader("Remote-User");
    String groups = request.getHeader("Remote-Group");
    String displayName = request.getHeader("Remote-Name");
    String email = request.getHeader("Remote-Email");

    if (username == null || groups == null || displayName == null || email == null) {
      return null;

    }

    return new AutheliaUser(username, groups, displayName, email);
  }

}
