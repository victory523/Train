package mucsi96.traininglog.weight;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Weight {
    @Id
    private ZonedDateTime createdAt;

    @Column
    private double weight;

    @Column
    private double fatMassWeight;

    @Column
    private double fatRatio;
}
