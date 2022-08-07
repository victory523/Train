package mucsi96.trainingLog;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.config.WebConfig;
import mucsi96.trainingLog.google.oauth.GoogleClient;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final WebConfig webConfig;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    @GetMapping("/logine")
    public String login(
            HttpServletRequest request,
            OAuth2AuthenticationToken auth2AuthenticationToken
    ) {
        if (auth2AuthenticationToken == null) {
            throw new ClientAuthorizationRequiredException(GoogleClient.id);
        }

        OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.loadAuthorizedClient(
                WithingsClient.id,
                auth2AuthenticationToken,
                request
        );

        if (authorizedClient == null) {
            throw  new ClientAuthorizationRequiredException(WithingsClient.id);
        }

        return "redirect:"+ webConfig.getPublicAppUrl();
    }
}
