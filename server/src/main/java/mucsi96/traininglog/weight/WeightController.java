package mucsi96.traininglog.weight;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.api.WeightResponse;

@RestController
@RequestMapping(value = "/weight", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RolesAllowed("user")
public class WeightController {

  private final WeightService weightService;

  @GetMapping
  WeightResponse weight() {
    Double weight = weightService.getTodayWeight().map(Weight::getValue).orElse(null);
    return WeightResponse
        .builder()
        .weight(weight)
        .build();
  }
}
