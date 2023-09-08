package mucsi96.traininglog.rides;

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
public class Ride {
  @Id
  private ZonedDateTime createdAt;

  @Column
  private String name;

  @Column
  private int movingTime;

  @Column
  private float distance;

  @Column
  private float totalElevationGain;

  @Column
  private float weightedAverageWatts;

  @Column
  private float calories;

  @Column
  private String sportType;
}
