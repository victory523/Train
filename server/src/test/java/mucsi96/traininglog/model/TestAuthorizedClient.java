package mucsi96.traininglog.model;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
  private byte[] accessTokenValue;
  private String accessTokenIssuedAt;
  private String accessTokenExpiresAt;
  private String accessTokenScopes;
  private byte[] refreshTokenValue;
  private String refreshTokenIssuedAt;
}
