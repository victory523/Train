package mucsi96.traininglog.withings.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Measure implements Serializable {
    private int value;
    private int type;
    private int unit;
}
