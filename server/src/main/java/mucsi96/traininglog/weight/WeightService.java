package mucsi96.traininglog.weight;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

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

  public Optional<Weight> getTodayWeight() {
    Instant startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.UTC);
    Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toInstant(ZoneOffset.UTC);
    log.info("Getting today weight from {} to {}", startTime, endTime);
    List<Weight> weights = weightRepository.findAllByCreatedAtBetween(startTime, endTime);
    Optional<Weight> result = weights.stream().findFirst();
    log.info("Got {}", result.isPresent() ? result.get().getValue() : "null");
    return result;
  }
}
