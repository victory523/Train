package mucsi96.traininglog.weight;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeightService {
  private final WeightRepository weightRepository;
  private final Clock clock;

  public void saveWeight(Weight weight) {
    log.info("persisting weight in db with value {}", weight.getValue());
    weightRepository.save(weight);
  }

  public List<Weight> getWeight(Optional<Integer> period, ZoneId zoneId) {
    ZonedDateTime endTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS).plusDays(1);
    return period.map(days -> {
      ZonedDateTime startTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS).minusDays(days - 1);
      log.info("Getting weight measurements from {} to {}", startTime, endTime);
      return weightRepository.findByCreatedAtBetween(startTime, endTime, Sort.by(Sort.Direction.ASC, "createdAt"));
    }).orElseGet(() -> {
      log.info("Getting weight measurements before {}", endTime);
      return weightRepository.findByCreatedAtBefore(endTime, Sort.by(Sort.Direction.ASC, "createdAt"));
    });
  }
}
