package mucsi96.trainingLog.withings;

import lombok.Data;
import mucsi96.trainingLog.withings.oauth.WithingsClient;

@Data
public class WithingsUnauthorizedException extends RuntimeException {
    private final String registrationId = WithingsClient.id;
}
