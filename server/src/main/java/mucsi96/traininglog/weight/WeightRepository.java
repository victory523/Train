package mucsi96.traininglog.weight;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightRepository extends JpaRepository<Weight, Long> {
  List<Weight> findByCreatedAtBetween(Instant startTime, Instant endTime, Sort sort);
  List<Weight> findByCreatedAtBefore(Instant endTime, Sort sort);
}
