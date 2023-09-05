package mucsi96.traininglog.fitness;

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
public class Fitness {
  @Id
  private ZonedDateTime createdAt;

  @Column
  private float fitness;

  @Column
  private float fatigue;

  @Column
  private float form;
}
