package mucsi96.traininglog.core;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import mucsi96.traininglog.withings.WithingsAuthorizationException;
import mucsi96.traininglog.withings.WithingsController;

@ControllerAdvice
public class AppControllerAdvice {

  @ExceptionHandler(WithingsAuthorizationException.class)
  public ResponseEntity<RepresentationModel<?>> handleClientAuthorizationRequired(
      WithingsAuthorizationException ex) {

    Link oauth2LogLink = WebMvcLinkBuilder
        .linkTo(WebMvcLinkBuilder.methodOn(WithingsController.class).authorize(null, null, null))
        .withRel("oauth2Login");

    RepresentationModel<?> model = RepresentationModel.of(null).add(oauth2LogLink);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(model);
  }
}
