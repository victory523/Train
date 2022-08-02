package mucsi96.trainingLog;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.config.SecurityConfig;
import mucsi96.trainingLog.config.WebConfig;
import mucsi96.trainingLog.withings.WithingsTechnicalException;
import mucsi96.trainingLog.withings.WithingsUnauthorizedException;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SecurityConfig securityConfig;
    static class EmptyModel extends RepresentationModel<EmptyModel> {}

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WithingsTechnicalException.class)
    public void handleTechnicalException() {}

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(WithingsUnauthorizedException.class)
    public @ResponseBody EmptyModel handleUnauthorizedException(WithingsUnauthorizedException exception) {
        EmptyModel model = new EmptyModel();
        model.add(
                Link.of(securityConfig.getOauth2LoginUrl(exception.getRegistrationId()), "oauth2Login")
        );
        return model;
    }
}
