package mucsi96.traininglog.withings.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MeasureGroup implements Serializable {
    @JsonProperty("grpid")
    private long grpId;
    private int attrib;
    private int date;
    private int created;
    private int category;
    @JsonProperty("deviceid")
    private String deviceId;
    private List<Measure> measures;
    private String comment;
    private String timezone;
}
