package mucsi96.traininglog.weight;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
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

  public void saveWeight(Weight weight) {
    log.info("persisting weight in db with value {}", weight.getValue());
    weightRepository.save(weight);
  }

  public List<Weight> getWeight(Optional<Integer> period) {
    Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toInstant(ZoneOffset.UTC);
    return period.map(days -> {
      Instant startTime = LocalDateTime.of(LocalDate.now().minusDays(days - 1), LocalTime.MIN).toInstant(ZoneOffset.UTC);
      log.info("Getting weight measurements from {} to {}", startTime, endTime);
      return weightRepository.findByCreatedAtBetween(startTime, endTime, Sort.by(Sort.Direction.ASC, "createdAt"));
    }).orElseGet(() -> {
      log.info("Getting weight measurements before {}", endTime);
      return weightRepository.findByCreatedAtBefore(endTime, Sort.by(Sort.Direction.ASC, "createdAt"));
    });
  }
}
