package mucsi96.trainingLog.oauth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UnauthorizedException extends RuntimeException {
  private final String registrationId;
}
