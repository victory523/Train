package mucsi96.trainingfog.withings.data;

import lombok.Data;

@Data
public class GetMeasureResponse {
    int status;
    GetMeasureResponseBody body;
}
