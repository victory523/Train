package mucsi96.trainingLog.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "oauth2_authorized_client")
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
public class TestAuthorizedClient {
  @Id
  private String clientRegistrationId;
  private String principalName;
  private String accessTokenType;
  private String accessTokenValue;
  private String accessTokenIssuedAt;
  private String accessTokenExpiresAt;
  private String accessTokenScopes;
  private String refreshTokenValue;
  private String refreshTokenIssuedAt;
}
