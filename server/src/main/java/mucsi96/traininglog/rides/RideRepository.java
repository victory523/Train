package mucsi96.traininglog.rides;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, ZonedDateTime> {
  List<Ride> findByCreatedAtBetween(ZonedDateTime startTime, ZonedDateTime endTime, Sort sort);
  List<Ride> findByCreatedAtBefore(ZonedDateTime endTime, Sort sort);
}
