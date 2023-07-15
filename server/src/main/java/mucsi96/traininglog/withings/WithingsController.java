package mucsi96.traininglog.withings;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

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

    try {
      OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(principal, servletRequest, servletResponse);
      if (!weightService.getTodayWeight().isPresent()) {
        withingsService.getTodayWeight(authorizedClient).ifPresent(weightService::saveWeight);
      }
    } catch (ClientAuthorizationRequiredException ex) {
      throw new WithingsAuthorizationException();
    }

  }

  @GetMapping("/authorize")
  public RedirectView authorize(
      Authentication principal,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    getAuthorizedClient(principal, servletRequest, servletResponse);
    return new RedirectView("/");
  }

  private OAuth2AuthorizedClient getAuthorizedClient(Authentication principal, HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId(WithingsClient.id)
        .principal(principal)
        .attribute(HttpServletRequest.class.getName(), servletRequest)
        .attribute(HttpServletResponse.class.getName(), servletResponse)
        .build();
    OAuth2AuthorizedClient authorizedClient = withingsAuthorizedClientManager.authorize(authorizeRequest);
    return authorizedClient;
  }
}
