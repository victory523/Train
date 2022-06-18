package mucsi96.trainingLog.withings.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetAccessTokenResponse implements Serializable {
    private int status;
    private GetAccessTokenResponseBody body;
}
