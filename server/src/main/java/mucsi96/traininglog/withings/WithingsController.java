package mucsi96.traininglog.withings;

import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.withings.data.WeightResponse;
import mucsi96.traininglog.withings.oauth.WithingsClient;

@Controller
@RequestMapping("/withings")
@RequiredArgsConstructor
@RolesAllowed("user")
public class WithingsController {

  private final WithingsService withingsService;

  @GetMapping(value = "/weight", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  WeightResponse weight(
    @Parameter(hidden = true)
    @RegisteredOAuth2AuthorizedClient(WithingsClient.id) OAuth2AuthorizedClient withingsAuthorizedClient
  ) {
    return WeightResponse
      .builder()
      .weight(withingsService.getFirstMeasureValue(withingsService.getMeasure(withingsAuthorizedClient)))
      .build();
  }
}
