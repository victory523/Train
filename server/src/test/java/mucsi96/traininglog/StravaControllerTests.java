package mucsi96.traininglog;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import mucsi96.traininglog.rides.Ride;
import mucsi96.traininglog.rides.RideRepository;
import mucsi96.traininglog.strava.StravaConfiguration;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class StravaControllerTests extends BaseIntegrationTest {

  private final MockMvc mockMvc;
  private final TestAuthorizedClientRepository authorizedClientRepository;
  private final RideRepository rideRepository;

  @LocalServerPort
  private int port;

  @RegisterExtension
  static WireMockExtension mockStravaServer = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicPort())
      .build();

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {

    registry.add("spring.security.oauth2.client.provider.strava.authorization-uri",
        () -> mockStravaServer.baseUrl() + "/oauth/authorize");
    registry.add(
        "spring.security.oauth2.client.provider.strava.token-uri",
        () -> mockStravaServer.baseUrl() + "/oauth/token");

    registry.add("spring.security.oauth2.client.registration.strava-client.client-id",
        () -> "test-strava-client-id");
    registry.add("spring.security.oauth2.client.registration.strava-client.client-secret",
        () -> "test-strava-client-secret");
    registry.add("strava.api-uri", () -> mockStravaServer.baseUrl());
  }

  @AfterEach
  void afterEach() {
    authorizedClientRepository.deleteAll();
    rideRepository.deleteAll();
  }

  private void authorizeStravaOAuth2Client() {
    TestAuthorizedClient authorizedClient = TestAuthorizedClient.builder()
        .clientRegistrationId("strava-client")
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
            post("/strava/sync")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$._links.oauth2Login.href", String.class))
        .isEqualTo("http://localhost/strava/authorize");
  }

  @Test
  public void returns_forbidden_if_user_has_no_user_role() throws Exception {
    authorizeStravaOAuth2Client();
    MockHttpServletResponse response = mockMvc
        .perform(
            post("/strava/sync")
                .headers(getHeaders("guest")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void redirects_to_strava_request_authorization_page() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/strava/authorize").headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(302);
    URI redirectUrl = new URI(response.getRedirectedUrl());
    assertThat(redirectUrl).hasHost("localhost");
    assertThat(redirectUrl).hasPort(mockStravaServer.getPort());
    assertThat(redirectUrl).hasPath("/oauth/authorize");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.RESPONSE_TYPE, "code");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.CLIENT_ID, "test-strava-client-id");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.SCOPE, "activity:read");
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.STATE);
    assertThat(redirectUrl).hasParameter(OAuth2ParameterNames.REDIRECT_URI,
        "http://localhost/strava/authorize");
  }

  @Test
  public void requests_access_token_after_consent_is_granted() throws Exception {
    mockStravaServer.stubFor(WireMock.post("/oauth/token").willReturn(
        WireMock.aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("strava-authorize.json")));

    MockHttpSession mockHttpSession = new MockHttpSession();
    MockHttpServletResponse response1 = mockMvc.perform(
        get("/strava/authorize").headers(getHeaders("user"))
            .session(mockHttpSession))
        .andReturn().getResponse();
    UriComponents components = UriComponentsBuilder.fromUriString(response1.getRedirectedUrl()).build();
    String state = URLDecoder.decode(
        components.getQueryParams().getFirst(OAuth2ParameterNames.STATE),
        StandardCharsets.UTF_8);

    MockHttpServletResponse response2 = mockMvc
        .perform(get(components.getQueryParams().getFirst(OAuth2ParameterNames.REDIRECT_URI))
            .headers(getHeaders("user"))
            .queryParam(OAuth2ParameterNames.STATE, state)
            .queryParam(OAuth2ParameterNames.CODE, "test-authorization-code")
            .session(mockHttpSession))
        .andReturn().getResponse();

    assertThat(response2.getStatus()).isEqualTo(302);
    assertThat(response2.getRedirectedUrl()).isEqualTo("http://localhost/strava/authorize");

    List<LoggedRequest> requests = mockStravaServer
        .findAll(WireMock.postRequestedFor(WireMock.urlEqualTo("/oauth/token")));
    assertThat(requests).hasSize(1);
    URI uri = new URI("?" + requests.get(0).getBodyAsString());

    Optional<TestAuthorizedClient> authorizedClient = authorizedClientRepository
        .findById(StravaConfiguration.registrationId);

    assertThat(authorizedClient.isPresent()).isTrue();
    assertThat(authorizedClient.get().getPrincipalName()).isEqualTo("rob");
    assertThat(new String(authorizedClient.get().getAccessTokenValue(), StandardCharsets.UTF_8))
        .isEqualTo("test-access-token");
    assertThat(new String(authorizedClient.get().getRefreshTokenValue(), StandardCharsets.UTF_8))
        .isEqualTo("test-refresh-token");
    assertThat(uri).hasParameter(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CODE, "test-authorization-code");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CLIENT_ID, "test-strava-client-id");
    assertThat(uri).hasParameter(OAuth2ParameterNames.CLIENT_SECRET, "test-strava-client-secret");
  }

  @Test
  public void requests_new_access_token_if_its_expired() throws Exception {
    mockStravaServer.stubFor(WireMock.post("/oauth/token").willReturn(
        WireMock.aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("strava-authorize.json")));
    mockStravaServer.stubFor(WireMock
        .get("/api/v3/athlete/activities?after=1945137600&before=1945224000")
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("strava-activities.json")));
    mockStravaServer.stubFor(WireMock
        .get("/api/v3/activities/1")
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("strava-activity.json")));
    mockStravaServer.stubFor(WireMock
        .get("/api/v3/activities/2")
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("strava-activity.json")));

    authorizeStravaOAuth2Client();
    LocalDateTime expiredAccessTokenIssuedAt = LocalDateTime.now().minusDays(2);
    LocalDateTime expiredAccessTokenExpiresAt = LocalDateTime.now().minusDays(1);
    authorizedClientRepository.findById(StravaConfiguration.registrationId).ifPresent(authorizedClient -> {
      authorizedClient.setAccessTokenValue("expired-access-token".getBytes(StandardCharsets.UTF_8));
      authorizedClient.setAccessTokenIssuedAt(expiredAccessTokenIssuedAt);
      authorizedClient.setAccessTokenExpiresAt(expiredAccessTokenExpiresAt);
      authorizedClientRepository.save(authorizedClient);
    });

    mockMvc
        .perform(
            post("/strava/sync")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    Optional<TestAuthorizedClient> authorizedClient = authorizedClientRepository
        .findById(StravaConfiguration.registrationId);

    assertThat(authorizedClient.isPresent()).isTrue();
    assertThat(authorizedClient.get().getPrincipalName()).isEqualTo("rob");
    assertThat(new String(authorizedClient.get().getAccessTokenValue(), StandardCharsets.UTF_8))
        .isEqualTo("test-access-token");
    assertThat(authorizedClient.get().getAccessTokenIssuedAt()).isAfter(expiredAccessTokenIssuedAt);
    assertThat(authorizedClient.get().getAccessTokenExpiresAt()).isAfter(expiredAccessTokenExpiresAt);
  }

  @Test
  public void returns_not_authorized_if_refresh_token_is_invalid() throws Exception {
    mockStravaServer.stubFor(WireMock.post("/oauth/token").willReturn(
        WireMock.aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": 1}")));

    authorizeStravaOAuth2Client();
    LocalDateTime expiredAccessTokenIssuedAt = LocalDateTime.now().minusDays(2);
    LocalDateTime expiredAccessTokenExpiresAt = LocalDateTime.now().minusDays(1);
    authorizedClientRepository.findById(StravaConfiguration.registrationId).ifPresent(authorizedClient -> {
      authorizedClient.setAccessTokenValue("expired-access-token".getBytes(StandardCharsets.UTF_8));
      authorizedClient.setAccessTokenIssuedAt(expiredAccessTokenIssuedAt);
      authorizedClient.setAccessTokenExpiresAt(expiredAccessTokenExpiresAt);
      authorizedClientRepository.save(authorizedClient);
    });

    MockHttpServletResponse response = mockMvc
        .perform(
            post("/strava/sync")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    Optional<TestAuthorizedClient> authorizedClient = authorizedClientRepository
        .findById(StravaConfiguration.registrationId);

    assertThat(authorizedClient.isPresent()).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$._links.oauth2Login.href", String.class))
        .isEqualTo("http://localhost/strava/authorize");
  }

  @Test
  public void pulls_todays_weight_from_strava_to_database() throws Exception {
    authorizeStravaOAuth2Client();
    mockStravaServer.stubFor(WireMock
        .get("/api/v3/athlete/activities?after=1945137600&before=1945224000")
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("strava-activities.json")));
    mockStravaServer.stubFor(WireMock
        .get("/api/v3/activities/1")
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("strava-activity.json")));
    mockStravaServer.stubFor(WireMock
        .get("/api/v3/activities/2")
        .willReturn(
            WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("strava-activity.json")));

    MockHttpServletResponse response = mockMvc
        .perform(
            post("/strava/sync")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(200);
    Optional<Ride> ride = rideRepository.findAll().stream().findFirst();
    assertThat(ride.isPresent()).isTrue();
    assertThat(ride.get().getCreatedAt().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
        .isEqualTo("2018-02-16T14:52:54Z[Etc/UTC]");
    assertThat(ride.get().getName()).isEqualTo("Happy Friday");
    assertThat(ride.get().getMovingTime()).isEqualTo(4207);
    assertThat(ride.get().getDistance()).isEqualTo(28099.0f);
    assertThat(ride.get().getTotalElevationGain()).isEqualTo(516.0f);
    assertThat(ride.get().getWeightedAverageWatts()).isEqualTo(230.0f);
    assertThat(ride.get().getCalories()).isEqualTo(870.2f);
    assertThat(ride.get().getSportType()).isEqualTo("MountainBikeRide");
  }
}
