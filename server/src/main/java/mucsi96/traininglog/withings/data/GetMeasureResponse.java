package mucsi96.traininglog.withings.data;

import lombok.Data;

@Data
public class GetMeasureResponse {
    int status;
    GetMeasureResponseBody body;
}
