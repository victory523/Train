package mucsi96.traininglog.withings;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.weight.WeightService;
import mucsi96.traininglog.withings.oauth.WithingsClient;

@RestController
@RequestMapping("/withings")
@RequiredArgsConstructor
@RolesAllowed("user")
public class WithingsController {

  private final WithingsService withingsService;
  private final WeightService weightService;
  private final OAuth2AuthorizedClientManager withingsAuthorizedClientManager;

  @PostMapping("/sync")
  public void sync(
      Authentication principal,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId(WithingsClient.id)
        .principal(principal)
        .attribute(HttpServletRequest.class.getName(), servletRequest)
        .attribute(HttpServletResponse.class.getName(), servletResponse)
        .build();

    try {
      OAuth2AuthorizedClient authorizedClient = withingsAuthorizedClientManager.authorize(authorizeRequest);
      if (!weightService.getTodayWeight().isPresent()) {
        withingsService.getTodayWeight(authorizedClient).ifPresent(weightService::saveWeight);
      }
    } catch (ClientAuthorizationRequiredException ex) {
      throw new WithingsAuthorizationException();
    }

  }

  @GetMapping("/authorize")
  public RedirectView authorize(
      @Parameter(hidden = true) @RegisteredOAuth2AuthorizedClient(WithingsClient.id) OAuth2AuthorizedClient withingsAuthorizedClient) {
    return new RedirectView("/");
  }
}
