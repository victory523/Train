package mucsi96.traininglog.withings;

import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.weight.WeightService;
import mucsi96.traininglog.withings.oauth.WithingsClient;

@RestController
@RequestMapping(value = "/withings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RolesAllowed("user")
public class WithingsController {

  private final WithingsService withingsService;
  private final WeightService weightService;

  @PostMapping("/sync")
  void sync(
      @Parameter(hidden = true) @RegisteredOAuth2AuthorizedClient(WithingsClient.id) OAuth2AuthorizedClient withingsAuthorizedClient) {

    if (!weightService.getTodayWeight().isPresent()) {
      withingsService.getTodayWeight(withingsAuthorizedClient).ifPresent(weightService::saveWeight);
    }
  }
}
