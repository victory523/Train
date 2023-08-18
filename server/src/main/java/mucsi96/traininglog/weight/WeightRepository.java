package mucsi96.traininglog.weight;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightRepository extends JpaRepository<Weight, Long> {
  List<Weight> findByCreatedAtBetween(ZonedDateTime startTime, ZonedDateTime endTime, Sort sort);
  List<Weight> findByCreatedAtBefore(ZonedDateTime endTime, Sort sort);
}
