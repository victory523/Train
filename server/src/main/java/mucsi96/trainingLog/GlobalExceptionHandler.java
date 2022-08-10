package mucsi96.trainingLog;

import mucsi96.trainingLog.oauth.UnauthorizedException;
import mucsi96.trainingLog.withings.WithingsTechnicalException;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(WithingsTechnicalException.class)
  public void handleTechnicalException() {
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(UnauthorizedException.class)
  public @ResponseBody RepresentationModel handleUnauthorizedException(UnauthorizedException exception) {
    return RepresentationModel
      .of(null)
      .add(WebMvcLinkBuilder.linkTo(
        WebMvcLinkBuilder.methodOn(HomeController.class).login(null, null)
      ).withRel("oauth2Login"));
  }
}
