package mucsi96.traininglog.withings.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeightResponse {
    private Double weight;
}
