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

@Service
@RequiredArgsConstructor
public class WeightService {
private final WeightRepository weightRepository;

  public void saveWeight(Weight weight) {
    weightRepository.save(weight);
  }

  public Optional<Weight> getTodayWeight() {
    Instant startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.UTC);
    Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toInstant(ZoneOffset.UTC);
    List<Weight> weights = weightRepository.findAllByCreatedAtBetween(startTime, endTime);
    return weights.stream().findFirst();
  }
}
