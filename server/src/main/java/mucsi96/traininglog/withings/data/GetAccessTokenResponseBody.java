package mucsi96.traininglog.withings.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetAccessTokenResponseBody implements Serializable {
    @JsonProperty("userid")
    private String userId;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("csrf_token")
    private String csrfToken;

    @JsonProperty("token_type")
    private String tokenType;
}
