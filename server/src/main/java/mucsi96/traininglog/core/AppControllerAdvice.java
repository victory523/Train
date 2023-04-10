package mucsi96.traininglog.core;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AppControllerAdvice {
  @ExceptionHandler({ClientAuthorizationRequiredException.class, ClientAuthorizationException.class})
  public ResponseEntity<RepresentationModel> handleClientAuthorizationRequired() {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(RepresentationModel
            .of(null)
            .add(Link.of("/withings/authenticate").withRel("oauth2Login")));
  }
}
