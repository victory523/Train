package mucsi96.trainingLog;

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
public class GlobalExceptionHandler {
    static class EmptyModel extends RepresentationModel<EmptyModel> {}

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WithingsTechnicalException.class)
    public void handleTechnicalException() {}

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(WithingsUnauthorizedException.class)
    public @ResponseBody EmptyModel handleUnauthorizedException() {
        EmptyModel model = new EmptyModel();
        model.add(
                Link.of("/api/oauth2/authorization/" + WithingsClient.id, "oauth2Login")
        );
        return model;
    }
}
