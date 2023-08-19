package mucsi96.traininglog.weight;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
  @Parameters({
      @Parameter(name = "period", example = "1"),
      @Parameter(in = ParameterIn.HEADER, name = "X-Timezone", required = true, example = "America/New_York")
  })
  List<WeightMeasurement> weight(
      @RequestParam(required = false) @Positive Integer period,
      @RequestHeader("X-Timezone") ZoneId zoneId) {
    return weightService.getWeight(Optional.ofNullable(period), zoneId).stream().map(measurement -> WeightMeasurement
        .builder()
        .date(measurement.getCreatedAt().withZoneSameInstant(zoneId).toOffsetDateTime())
        .weight(measurement.getWeight())
        .fatRatio(measurement.getFatRatio())
        .fatMassWeight(measurement.getFatMassWeight())
        .build()).toList();
  }
}
