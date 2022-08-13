package mucsi96.trainingLog;

import mucsi96.trainingLog.google.oauth.GoogleClient;
import mucsi96.trainingLog.oauth.UnauthorizedException;
import mucsi96.trainingLog.withings.WithingsTechnicalException;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(WithingsTechnicalException.class)
  public void handleTechnicalException() {
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(UnauthorizedException.class)
  public @ResponseBody RepresentationModel handleUnauthorizedException(UnauthorizedException exception) {
    String oauth2LoginUrl = ServletUriComponentsBuilder.fromCurrentServletMapping().path(
      OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + exception.getRegistrationId()
    ).build().toString();
    return RepresentationModel
      .of(null)
      .add(Link.of(oauth2LoginUrl).withRel("oauth2Login"));
  }
}
