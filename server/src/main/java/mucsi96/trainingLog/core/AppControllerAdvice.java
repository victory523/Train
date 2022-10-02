package mucsi96.trainingLog.core;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.oauth.AuthorizationRequestUtil;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestControllerAdvice
public class AppControllerAdvice {

  @ExceptionHandler(ClientAuthorizationRequiredException.class)
  public ResponseEntity<RepresentationModel> handleClientAuthorizationRequired(ClientAuthorizationRequiredException ex) {
    String oauth2LoginUrl = ServletUriComponentsBuilder.fromCurrentServletMapping().path(
      OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + ex.getClientRegistrationId()
    ).build().toString();
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(RepresentationModel
        .of(null)
        .add(Link.of(oauth2LoginUrl).withRel("oauth2Login")));
  }
}
