package mucsi96.traininglog.weight;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.api.WeightMeasurement;

@RestController
@RequestMapping(value = "/weight", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RolesAllowed("user")
public class WeightController {

  private final WeightService weightService;

  @GetMapping
  List<WeightMeasurement> weight(@RequestParam(required = false) @Positive Integer period) {
    return weightService.getWeight(Optional.ofNullable(period)).stream().map(measurement -> WeightMeasurement
        .builder()
        .weight(measurement.getValue())
        .date(OffsetDateTime.ofInstant(measurement.getCreatedAt(), ZoneOffset.UTC))
        .build()).toList();
  }
}
