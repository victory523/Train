package mucsi96.trainingfog.core;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice
public class AppControllerAdvice {

  @ExceptionHandler(PreAuthenticatedCredentialsNotFoundException.class)
  public ResponseEntity handlePreAuthenticatedCredentialsNotFoundException() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(null);
  }

  @ExceptionHandler(ClientAuthorizationRequiredException.class)
  public ResponseEntity<RepresentationModel> handleClientAuthorizationRequired(
      ClientAuthorizationRequiredException ex) {
    String oauth2LoginUrl = ServletUriComponentsBuilder.fromCurrentServletMapping().path(
        OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/"
            + ex.getClientRegistrationId())
        .build().toString();
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(RepresentationModel
            .of(null)
            .add(Link.of(oauth2LoginUrl).withRel("oauth2Login")));
  }
}
