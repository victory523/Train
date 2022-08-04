package mucsi96.trainingLog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WithingsControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void returns_unauthorized_if_bearer_token_is_not_sent() {
        webTestClient
                .get()
                .uri("/withings/weight")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                    .jsonPath("$._links.oauth2Login.href")
                    .isEqualTo("/api/oauth2/authorization/withings-client");
    }

    @Test
    public void redirects_to_withings_oauth_page_on_calling_oauth2Login() {
        webTestClient
                .get()
                .uri("/oauth2/authorization/withings-client")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value(HttpHeaders.LOCATION, location -> {
                    UriComponents components = UriComponentsBuilder.fromUriString(location).build();
                    assertEquals("localhost", components.getHost());
                    assertEquals(8080, components.getPort());
                    assertEquals("/oauth2_user/authorize2", components.getPath());
                    assertEquals(
                            "code",
                            components.getQueryParams().get(OAuth2ParameterNames.RESPONSE_TYPE).get(0)
                    );
                    assertEquals(
                            "test-withings-client-id",
                            components.getQueryParams().get(OAuth2ParameterNames.CLIENT_ID).get(0)
                    );
                    assertEquals(
                            "user.metrics",
                            components.getQueryParams().get(OAuth2ParameterNames.SCOPE).get(0)
                    );
                    assertEquals(
                            Base64.getUrlDecoder().decode(
                                    URLDecoder.decode(
                                            components.getQueryParams().get(OAuth2ParameterNames.STATE).get(0),
                                            StandardCharsets.UTF_8
                                    )
                            ).length,
                            32
                    );
                });
    }
}
