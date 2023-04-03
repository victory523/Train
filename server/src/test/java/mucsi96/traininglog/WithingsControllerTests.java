package mucsi96.traininglog;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.jayway.jsonpath.JsonPath;

import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.model.TestAuthorizedClient;
import mucsi96.traininglog.repository.TestAuthorizedClientRepository;
import mucsi96.traininglog.withings.oauth.WithingsClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class WithingsControllerTests extends BaseIntegrationTest {

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
        () -> withingsServer.baseUrl() + "/v2/oauth2");
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = getAuthHeaders("user");
    headers.add("X-Forwarded-Host", "training-log.com");
    headers.add("X-Forwarded-Port", "3000");
    headers.add("X-Forwarded-Proto", "https");
    headers.add("X-Forwarded-Prefix", "/api");
    return headers;
  }

  @AfterEach
  void afterEach() {
    authorizedClientRepository.deleteAll();
  }

  @Test
  public void returns_not_authorized_if_no_preauth_headers_are_sent() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/withings/weight"))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void returns_not_authorized_if_authorized_client_is_not_found() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/withings/weight")
                .headers(getAuthHeaders("guest")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$._links.oauth2Login.href", String.class))
        .isEqualTo("http://localhost/oauth2/authorization/withings-client");
  }

  @Test
  public void redirects_to_withings_request_authorization_page() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/oauth2/authorization/withings-client")
                .headers(getHeaders()))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(302);
    URI redirectUrl = new URI(response.getRedirectedUrl());
    assertThat(redirectUrl).hasHost("localhost");
    assertThat(redirectUrl).hasPort(8080);
    assertThat(redirectUrl).hasPath("/oauth2_user/authorize2");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.RESPONSE_TYPE, "code");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.CLIENT_ID, "test-withings-client-id");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.SCOPE, "user.metrics");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.STATE);
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.REDIRECT_URI, "https://training-log.com:3000/api/authorize/oauth2/code/withings-client");
  }

  @Test
  public void requests_withings_access_token_after_consent_is_granted() throws Exception {
    withingsServer.stubFor(WireMock.post("/v2/oauth2").willReturn(
        WireMock.aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("withings-authorize.json")));

    MockHttpSession mockHttpSession = new MockHttpSession();
    MockHttpServletResponse response1 = mockMvc.perform(
        get("/oauth2/authorization/withings-client")
            .headers(getHeaders())
            .session(mockHttpSession))
        .andReturn().getResponse();
    UriComponents components = UriComponentsBuilder.fromUriString(response1.getRedirectedUrl()).build();
    String state = URLDecoder.decode(
        components.getQueryParams().getFirst(OAuth2ParameterNames.STATE),
        StandardCharsets.UTF_8);

    MockHttpServletResponse response2 = mockMvc.perform(get("/authorize/oauth2/code/withings-client")
        .queryParam(OAuth2ParameterNames.STATE, state)
        .queryParam(OAuth2ParameterNames.CODE, "test-authorization-code")
        .headers(getHeaders())
        .session(mockHttpSession))
        .andReturn().getResponse();

    assertThat(response2.getStatus()).isEqualTo(302);
    assertThat(response2.getRedirectedUrl()).isEqualTo("https://training-log.com:3000/");

    List<LoggedRequest> requests = withingsServer.findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/v2/oauth2")));
    assertThat(requests).hasSize(1);
    URI uri = new URI("?" + requests.get(0).getBodyAsString());

    Optional<TestAuthorizedClient> authorizedClient = authorizedClientRepository.findById(WithingsClient.id);

    assertThat(authorizedClient.isPresent()).isTrue();
    assertThat(authorizedClient.get().getPrincipalName()).isEqualTo("rob");
    assertThat(authorizedClient.get().getAccessTokenValue()).isEqualTo("test-access-token");
    assertThat(authorizedClient.get().getRefreshTokenValue()).isEqualTo("test-refresh-token");
    assertThat(uri).hasParameter(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CODE, "test-authorization-code");
    assertThat(uri).hasParameter("action", "requesttoken");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CLIENT_ID, "test-withings-client-id");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CLIENT_SECRET, "test-withings-client-secret");
  }
}
