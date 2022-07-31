package mucsi96.trainingLog.withings.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class WithingsResponse<T> implements Serializable {
    private int status;
    private T body;
    private String error;
}
