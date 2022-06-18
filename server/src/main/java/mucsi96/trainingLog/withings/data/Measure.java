package mucsi96.trainingLog.withings.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Measure implements Serializable {
    private int value;
    private int type;
    private int unit;
}
