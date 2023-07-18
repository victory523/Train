package mucsi96.traininglog;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
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
import mucsi96.traininglog.weight.Weight;
import mucsi96.traininglog.weight.WeightRepository;
import mucsi96.traininglog.withings.WithingsConfiguration;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class WithingsControllerTests extends BaseIntegrationTest {

  private final MockMvc mockMvc;
  private final TestAuthorizedClientRepository authorizedClientRepository;
  private final WeightRepository weightRepository;

  @LocalServerPort
  private int port;

  @RegisterExtension
  static WireMockExtension mockWithingsServer = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicPort())
      .build();

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {

    registry.add("spring.security.oauth2.client.provider.withings.authorization-uri",
        () -> mockWithingsServer.baseUrl() + "/oauth2_user/authorize2");
    registry.add(
        "spring.security.oauth2.client.provider.withings.token-uri",
        () -> mockWithingsServer.baseUrl() + "/v2/oauth2");

    registry.add("spring.security.oauth2.client.registration.withings-client.client-id",
        () -> "test-withings-client-id");
    registry.add("spring.security.oauth2.client.registration.withings-client.client-secret",
        () -> "test-withings-client-secret");
    registry.add("withings.api.uri", () -> mockWithingsServer.baseUrl());
  }

  @AfterEach
  void afterEach() {
    authorizedClientRepository.deleteAll();
    weightRepository.deleteAll();
  }

  private void authorizeWithingsOAuth2Client() {
    TestAuthorizedClient authorizedClient = TestAuthorizedClient.builder()
        .clientRegistrationId("withings-client")
        .principalName("rob")
        .accessTokenType("Bearer")
        .accessTokenValue("test-access-token".getBytes(StandardCharsets.UTF_8))
        .accessTokenIssuedAt(LocalDateTime.now())
        .accessTokenExpiresAt(LocalDateTime.now().plusDays(1))
        .accessTokenScopes("user.metrics")
        .refreshTokenValue("test-refresh-token".getBytes(StandardCharsets.UTF_8))
        .refreshTokenIssuedAt(LocalDateTime.now())
        .build();

    authorizedClientRepository.save(authorizedClient);
  }

  @Test
  public void returns_not_authorized_if_authorized_client_is_not_found() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            post("/withings/sync")
                .headers(getAuthHeaders("user")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$._links.oauth2Login.href", String.class))
        .isEqualTo("http://localhost/withings/authorize");
  }

  @Test
  public void returns_forbidden_if_user_has_no_user_role() throws Exception {
    authorizeWithingsOAuth2Client();
    MockHttpServletResponse response = mockMvc
        .perform(
            post("/withings/sync")
                .headers(getAuthHeaders("guest")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void redirects_to_withings_request_authorization_page() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/withings/authorize").headers(getAuthHeaders("user")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(302);
    URI redirectUrl = new URI(response.getRedirectedUrl());
    assertThat(redirectUrl).hasHost("localhost");
    assertThat(redirectUrl).hasPort(mockWithingsServer.getPort());
    assertThat(redirectUrl).hasPath("/oauth2_user/authorize2");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.RESPONSE_TYPE, "code");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.CLIENT_ID, "test-withings-client-id");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.SCOPE, "user.metrics");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.STATE);
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.REDIRECT_URI,
        "http://localhost/withings/authorize");
  }

  @Test
  public void requests_access_token_after_consent_is_granted() throws Exception {
    mockWithingsServer.stubFor(WireMock.post("/v2/oauth2").willReturn(
        WireMock.aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("withings-authorize.json")));

    MockHttpSession mockHttpSession = new MockHttpSession();
    MockHttpServletResponse response1 = mockMvc.perform(
        get("/withings/authorize").headers(getAuthHeaders("user"))
            .session(mockHttpSession))
        .andReturn().getResponse();
    UriComponents components = UriComponentsBuilder.fromUriString(response1.getRedirectedUrl()).build();
    String state = URLDecoder.decode(
        components.getQueryParams().getFirst(OAuth2ParameterNames.STATE),
        StandardCharsets.UTF_8);

    MockHttpServletResponse response2 = mockMvc
        .perform(get(components.getQueryParams().getFirst(OAuth2ParameterNames.REDIRECT_URI))
            .headers(getAuthHeaders("user"))
            .queryParam(OAuth2ParameterNames.STATE, state)
            .queryParam(OAuth2ParameterNames.CODE, "test-authorization-code")
            .session(mockHttpSession))
        .andReturn().getResponse();

    assertThat(response2.getStatus()).isEqualTo(302);
    assertThat(response2.getRedirectedUrl()).isEqualTo("http://localhost/withings/authorize");

    List<LoggedRequest> requests = mockWithingsServer
        .findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/v2/oauth2")));
    assertThat(requests).hasSize(1);
    URI uri = new URI("?" + requests.get(0).getBodyAsString());

    Optional<TestAuthorizedClient> authorizedClient = authorizedClientRepository.findById(WithingsConfiguration.registrationId);

    assertThat(authorizedClient.isPresent()).isTrue();
    assertThat(authorizedClient.get().getPrincipalName()).isEqualTo("rob");
    assertThat(new String(authorizedClient.get().getAccessTokenValue(), StandardCharsets.UTF_8))
        .isEqualTo("test-access-token");
    assertThat(new String(authorizedClient.get().getRefreshTokenValue(), StandardCharsets.UTF_8))
        .isEqualTo("test-refresh-token");
    assertThat(uri).hasParameter(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CODE, "test-authorization-code");
    assertThat(uri).hasParameter("action", "requesttoken");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CLIENT_ID, "test-withings-client-id");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CLIENT_SECRET, "test-withings-client-secret");
  }

  @Test
  public void requests_new_access_token_if_its_expired() throws Exception {
    mockWithingsServer.stubFor(WireMock.post("/v2/oauth2").willReturn(
        WireMock.aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("withings-authorize.json")));
    long startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.UTC).getEpochSecond();
    long endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toInstant(ZoneOffset.UTC).getEpochSecond();
    mockWithingsServer.stubFor(WireMock
        .post(String.format("/measure?action=getmeas&meastype=1&category=1&startdate=%s&enddate=%s",
            startTime, endTime))
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("withings-measure.json")));

    authorizeWithingsOAuth2Client();
    LocalDateTime expiredAccessTokenIssuedAt = LocalDateTime.now().minusDays(2);
    LocalDateTime expiredAccessTokenExpiresAt = LocalDateTime.now().minusDays(1);
    authorizedClientRepository.findById(WithingsConfiguration.registrationId).ifPresent(authorizedClient -> {
      authorizedClient.setAccessTokenValue("expired-access-token".getBytes(StandardCharsets.UTF_8));
      authorizedClient.setAccessTokenIssuedAt(expiredAccessTokenIssuedAt);
      authorizedClient.setAccessTokenExpiresAt(expiredAccessTokenExpiresAt);
      authorizedClientRepository.save(authorizedClient);
    });

    mockMvc
        .perform(
            post("/withings/sync")
                .headers(getAuthHeaders("user")))
        .andReturn().getResponse();

    Optional<TestAuthorizedClient> authorizedClient = authorizedClientRepository.findById(WithingsConfiguration.registrationId);

    assertThat(authorizedClient.isPresent()).isTrue();
    assertThat(authorizedClient.get().getPrincipalName()).isEqualTo("rob");
    assertThat(new String(authorizedClient.get().getAccessTokenValue(), StandardCharsets.UTF_8))
        .isEqualTo("test-access-token");
    assertThat(authorizedClient.get().getAccessTokenIssuedAt()).isAfter(expiredAccessTokenIssuedAt);
    assertThat(authorizedClient.get().getAccessTokenExpiresAt()).isAfter(expiredAccessTokenExpiresAt);
  }

  @Test
  public void pulls_todays_weight_from_withings_to_database() throws Exception {
    authorizeWithingsOAuth2Client();
    long startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.UTC).getEpochSecond();
    long endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toInstant(ZoneOffset.UTC).getEpochSecond();
    mockWithingsServer.stubFor(WireMock
        .post(String.format("/measure?action=getmeas&meastype=1&category=1&startdate=%s&enddate=%s",
            startTime, endTime))
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("withings-measure.json")));

    MockHttpServletResponse response = mockMvc
        .perform(
            post("/withings/sync")
                .headers(getAuthHeaders("user")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(200);
    Optional<Weight> weight = weightRepository.findAll().stream().findFirst();
    assertThat(weight.isPresent()).isTrue();
    assertThat(weight.get().getValue()).isEqualTo(65.75);
    assertThat(weight.get().getCreatedAt().getEpochSecond()).isEqualTo(1594245600L);
  }
}
