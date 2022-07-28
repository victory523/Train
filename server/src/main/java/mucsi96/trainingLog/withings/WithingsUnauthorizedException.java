package mucsi96.trainingLog.withings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class WithingsUnauthorizedException extends RuntimeException {
}
