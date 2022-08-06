package mucsi96.trainingLog;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.config.WebConfig;
import mucsi96.trainingLog.google.oauth.GoogleClient;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final WebConfig webConfig;

    @GetMapping("/login")
    public RedirectView login(
            @RegisteredOAuth2AuthorizedClient(GoogleClient.id) OAuth2AuthorizedClient googleAuthorizedClient,
            @RegisteredOAuth2AuthorizedClient(WithingsClient.id) OAuth2AuthorizedClient withingsAuthorizedClient
    ) {
        return new RedirectView(webConfig.getPublicAppUrl());
    }
}
