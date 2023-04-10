package mucsi96.traininglog.withings;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.withings.data.WeightResponse;
import mucsi96.traininglog.withings.oauth.WithingsClient;

@Controller
@RequestMapping("/withings")
@RequiredArgsConstructor
@RolesAllowed("user")
public class WithingsController {

  private final WithingsService withingsService;

  @GetMapping(value = "/authenticate")
  void authenticate(
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      Authentication principal,
      OAuth2AuthorizedClientManager authorizedClientManager) throws IOException {
    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId(WithingsClient.id)
        .principal(principal)
        .attribute(HttpServletRequest.class.getName(), servletRequest)
        .attribute(HttpServletResponse.class.getName(), servletResponse)
        .build();

    try {
      authorizedClientManager.authorize(authorizeRequest);
    } catch (ClientAuthorizationRequiredException authorizationRequiredException) {
      servletResponse.sendRedirect(
          OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + WithingsClient.id);
    }

    servletResponse.sendRedirect("/");
  }

  @GetMapping(value = "/weight", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  WeightResponse weight(
      @Parameter(hidden = true) @RegisteredOAuth2AuthorizedClient(WithingsClient.id) OAuth2AuthorizedClient withingsAuthorizedClient) {
    return WeightResponse
        .builder()
        .weight(withingsService.getFirstMeasureValue(withingsService.getMeasure(withingsAuthorizedClient)))
        .build();
  }
}
