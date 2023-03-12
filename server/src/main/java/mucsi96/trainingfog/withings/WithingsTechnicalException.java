package mucsi96.trainingfog.withings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class WithingsTechnicalException extends RuntimeException {
}
