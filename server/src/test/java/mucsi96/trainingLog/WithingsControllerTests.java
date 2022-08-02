package mucsi96.trainingLog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WithingsControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void returnsUnauthorizedWithoutBearerToken() {
        webTestClient
                .get()
                .uri("/withings/weight")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                    .jsonPath("$._links.oauth2Login.href")
                    .isEqualTo("/api/oauth2/authorization/withings-client");
    }
}
