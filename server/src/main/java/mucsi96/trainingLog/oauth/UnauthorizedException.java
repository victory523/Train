package mucsi96.trainingLog.oauth;

import lombok.Data;
import mucsi96.trainingLog.withings.oauth.WithingsClient;

@Data
public class UnauthorizedException extends RuntimeException {
    private final String registrationId = WithingsClient.id;
}
