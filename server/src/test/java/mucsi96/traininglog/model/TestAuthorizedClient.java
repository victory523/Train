package mucsi96.traininglog.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder()
@Entity
@Table(name = "oauth2_authorized_client")
@AllArgsConstructor
@NoArgsConstructor
public class TestAuthorizedClient {
  @Id
  private String clientRegistrationId;
  private String principalName;
  private String accessTokenType;
  private byte[] accessTokenValue;
  private LocalDateTime accessTokenIssuedAt;
  private LocalDateTime accessTokenExpiresAt;
  private String accessTokenScopes;
  private byte[] refreshTokenValue;
  private LocalDateTime refreshTokenIssuedAt;
}
