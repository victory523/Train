package mucsi96.trainingLog.withings.oauth;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Builder
public class WithingsAuthorizedClient {
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Date issuedAt;
    private Date expiresAt;
    private Set<String> scopes;
}
