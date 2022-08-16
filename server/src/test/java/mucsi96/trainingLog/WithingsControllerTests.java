package mucsi96.trainingLog;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.model.TestAuthorizedClient;
import mucsi96.trainingLog.repository.TestAuthorizedClientRepository;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.assertj.core.matcher.AssertionMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class WithingsControllerTests {

  private final MockMvc mockMvc;
  private final TestAuthorizedClientRepository authorizedClientRepository;

  @RegisterExtension
  static WireMockExtension withingsServer = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add(
      "spring.security.oauth2.client.provider.withings.token-uri",
      () -> withingsServer.baseUrl() + "/v2/oauth2"
    );
  }

  private HttpHeaders getForwardHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Forwarded-Host", "training-log.com");
    headers.add("X-Forwarded-Port", "3000");
    headers.add("X-Forwarded-Proto", "https");
    headers.add("X-Forwarded-Prefix", "/api");
    return headers;
  }

  @Test
  @WithMockGoogleUser
  public void returns_unauthorized_if_bearer_token_is_not_sent() throws Exception {
    mockMvc
      .perform(
        get("/withings/weight")
          .headers(getForwardHeaders())
      )
      .andExpect(status().isUnauthorized())
      .andExpect(
        jsonPath("$._links.oauth2Login.href")
          .value("https://training-log.com:3000/api/oauth2/authorization/withings-client")
      );
  }

  @Test
  @WithMockGoogleUser
  public void redirects_to_withings_request_authorization_page() throws Exception {
      mockMvc
        .perform(
          get("/oauth2/authorization/withings-client")
            .headers(getForwardHeaders())
        )
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, new AssertionMatcher<>() {
          @Override
          public void assertion(String location) throws AssertionError {
            UriComponents components = UriComponentsBuilder.fromUriString(location).build();
            assertEquals("localhost", components.getHost());
            assertEquals(8080, components.getPort());
            assertEquals("/oauth2_user/authorize2", components.getPath());
            assertEquals(
              "code",
              components.getQueryParams().getFirst(OAuth2ParameterNames.RESPONSE_TYPE)
            );
            assertEquals(
              "test-withings-client-id",
              components.getQueryParams().getFirst(OAuth2ParameterNames.CLIENT_ID)
            );
            assertEquals(
              "user.metrics",
              components.getQueryParams().getFirst(OAuth2ParameterNames.SCOPE)
            );
            assertEquals(
              Base64.getUrlDecoder().decode(
                URLDecoder.decode(
                  components.getQueryParams().getFirst(OAuth2ParameterNames.STATE),
                  StandardCharsets.UTF_8
                )
              ).length,
              32
            );
            assertEquals(
              "https://training-log.com:3000/api/authorize/oauth2/code/withings-client",
              components.getQueryParams().getFirst(OAuth2ParameterNames.REDIRECT_URI)
            );
          }
        }));

  }

  @Test
  @WithMockGoogleUser
  public void requests_withings_access_token_after_consent_is_granted() throws Exception {
    withingsServer.stubFor(WireMock.post("/v2/oauth2").willReturn(
      WireMock.aResponse()
        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .withBodyFile("withings-authorize.json")
    ));

    MockHttpSession mockHttpSession = new MockHttpSession();
    MvcResult result = mockMvc.perform(
      get("/oauth2/authorization/withings-client")
        .headers(getForwardHeaders())
        .session(mockHttpSession)
    ).andReturn();
    UriComponents components = UriComponentsBuilder.fromUriString(
      result.getResponse().getHeader(HttpHeaders.LOCATION)
    ).build();
    String state = URLDecoder.decode(
      components.getQueryParams().getFirst(OAuth2ParameterNames.STATE),
      StandardCharsets.UTF_8
    );

    mockMvc.perform(get("/authorize/oauth2/code/withings-client")
        .session(mockHttpSession)
        .queryParam(OAuth2ParameterNames.STATE, state)
        .queryParam(OAuth2ParameterNames.CODE, "test-authorization-code")
        .headers(getForwardHeaders())
      )
      .andExpect(status().isFound())
      .andExpect(header().string(HttpHeaders.LOCATION, "https://training-log.com:3000/"));


    List<LoggedRequest> requests = withingsServer.findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/v2/oauth2")));
    assertEquals(1, requests.size());
    MultiValueMap<String, String> queryParams = UriComponentsBuilder
      .fromUriString("?" + requests.get(0).getBodyAsString())
      .build()
      .getQueryParams();

    Optional<TestAuthorizedClient> authorizedClient = authorizedClientRepository.findById(WithingsClient.id);

    assertTrue(authorizedClient.isPresent());
    assertEquals("rob", authorizedClient.get().getPrincipalName());
    assertEquals("test-access-token", authorizedClient.get().getAccessTokenValue());
    assertEquals("test-refresh-token", authorizedClient.get().getRefreshTokenValue());

    assertEquals("authorization_code", queryParams.getFirst(OAuth2ParameterNames.GRANT_TYPE));
    assertEquals("test-authorization-code", queryParams.getFirst(OAuth2ParameterNames.CODE));
    assertEquals("requesttoken", queryParams.getFirst("action"));
    assertEquals("test-withings-client-id", queryParams.getFirst(OAuth2ParameterNames.CLIENT_ID));
    assertEquals("test-withings-client-secret", queryParams.getFirst(OAuth2ParameterNames.CLIENT_SECRET));
  }
}
