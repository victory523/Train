package mucsi96.trainingLog.withings.data;

import lombok.Data;

@Data
public class GetMeasureResponse {
    int status;
    GetMeasureResponseBody body;
}
