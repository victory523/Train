package mucsi96.traininglog.strava;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StravaTechnicalException extends RuntimeException {
}
