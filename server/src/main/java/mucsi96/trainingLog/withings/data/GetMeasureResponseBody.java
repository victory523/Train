package mucsi96.trainingLog.withings.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetMeasureResponseBody {
    @JsonProperty("updatetime")
    String updateTime;
    String timezone;
    @JsonProperty("measuregrps")
    List<MeasureGroup> measureGroups;
    int more;
    int offset;
}
