package mucsi96.traininglog.withings;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mucsi96.traininglog.weight.WeightService;

@RestController
@RequestMapping("/withings")
@RequiredArgsConstructor
@RolesAllowed("user")
@Slf4j
public class WithingsController {

  private final WithingsService withingsService;
  private final WeightService weightService;
  private final OAuth2AuthorizedClientManager withingsAuthorizedClientManager;

  @PostMapping("/sync")
  @Operation(responses = { @ApiResponse(content = @Content()),
      @ApiResponse(responseCode = "401", content = @Content(), links = {
          @io.swagger.v3.oas.annotations.links.Link(name = "oauth2Login", operationId = "withings-authorize") }) })
  public ResponseEntity<RepresentationModel<?>> sync(
      Authentication principal,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {

    log.info("syncing from Withings");

    try {
      OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(principal, servletRequest, servletResponse);
      withingsService.getTodayWeight(authorizedClient).ifPresent(weightService::saveWeight);
    } catch (OAuth2AuthorizationException ex) {
      Link oauth2LogLink = linkTo(methodOn(WithingsController.class).authorize(null, null, null))
          .withRel("oauth2Login");

      RepresentationModel<?> model = RepresentationModel.of(null).add(oauth2LogLink);

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(model);
    }

    return ResponseEntity.ok(null);

  }

  @GetMapping("/authorize")
  @Operation(operationId = "withings-authorize", responses = { @ApiResponse(content = @Content()) })
  public RedirectView authorize(
      Authentication principal,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
        log.info("authorizing Withings client");
    getAuthorizedClient(principal, servletRequest, servletResponse);
    return new RedirectView("/");
  }

  private OAuth2AuthorizedClient getAuthorizedClient(Authentication principal, HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId(WithingsConfiguration.registrationId)
        .principal(principal)
        .attribute(HttpServletRequest.class.getName(), servletRequest)
        .attribute(HttpServletResponse.class.getName(), servletResponse)
        .build();
    OAuth2AuthorizedClient authorizedClient = withingsAuthorizedClientManager.authorize(authorizeRequest);
    return authorizedClient;
  }
}
