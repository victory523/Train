package mucsi96.trainingLog;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.config.SecurityConfig;
import mucsi96.trainingLog.config.WebConfig;
import mucsi96.trainingLog.withings.WithingsTechnicalException;
import mucsi96.trainingLog.oauth.UnauthorizedException;
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

    private final WebConfig webConfig;
    static class EmptyModel extends RepresentationModel<EmptyModel> {}

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WithingsTechnicalException.class)
    public void handleTechnicalException() {}

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public @ResponseBody EmptyModel handleUnauthorizedException(UnauthorizedException exception) {
        EmptyModel model = new EmptyModel();
        model.add(
                Link.of(webConfig.getBaseUrl() + "/logine", "oauth2Login")
        );
        return model;
    }
}
