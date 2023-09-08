package mucsi96.traininglog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import lombok.RequiredArgsConstructor;
import mucsi96.traininglog.rides.Ride;
import mucsi96.traininglog.rides.RideRepository;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class RideControllerTests extends BaseIntegrationTest {

  private final MockMvc mockMvc;
  private final RideRepository rideRepository;
  private final Clock clock;

  @BeforeEach
  void beforeEach() {
    rideRepository.deleteAll();
    rideRepository.save(Ride.builder()
        .createdAt(ZonedDateTime.now(clock).minus(400, ChronoUnit.DAYS))
        .name("Ride 1")
        .calories(546f)
        .distance(10747.7f)
        .movingTime(2074)
        .totalElevationGain(308)
        .weightedAverageWatts(204)
        .build());
    rideRepository.save(Ride.builder()
        .createdAt(ZonedDateTime.now(clock).minus(356, ChronoUnit.DAYS))
        .name("Ride 2")
        .build());
    rideRepository.save(Ride.builder()
        .createdAt(ZonedDateTime.now(clock).minus(6, ChronoUnit.DAYS))
        .name("Ride 3")
        .build());
    rideRepository.save(Ride.builder()
        .createdAt(ZonedDateTime.now(clock).minus(5, ChronoUnit.DAYS))
        .name("Ride 4")
        .build());
    rideRepository.save(Ride.builder()
        .createdAt(ZonedDateTime.now(clock))
        .name("Ride 5")
        .calories(646f)
        .distance(11747.7f)
        .movingTime(3074)
        .totalElevationGain(408)
        .weightedAverageWatts(224)
        .build());
    rideRepository.save(Ride.builder()
        .createdAt(ZonedDateTime.now(clock).minus(1, ChronoUnit.DAYS))
        .name("Ride 6")
        .build());
  }

  @Test
  public void returns_not_authorized_if_no_preauth_headers_are_sent() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/ride/stats"))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void returns_forbidden_if_user_has_no_user_role() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/ride/stats")
                .headers(getHeaders("guest")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void returns_today_ride_stats() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/ride/stats")
                .queryParam("period", "1")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.calories", Float.class)).isEqualTo(646.0f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.elevationGain", Float.class)).isEqualTo(408.0f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.distance", Float.class)).isEqualTo(11747.7f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.time", Integer.class)).isEqualTo(3074);
  }

  @Test
  public void returns_one_week_ride_stats() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/ride/stats")
                .queryParam("period", "7")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.calories", Float.class)).isEqualTo(646.0f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.elevationGain", Float.class)).isEqualTo(408.0f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.distance", Float.class)).isEqualTo(11747.7f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.time", Integer.class)).isEqualTo(3074);
  }

  @Test
  public void returns_all_time_ride_stats() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/ride/stats")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.calories", Float.class)).isEqualTo(1192.0f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.elevationGain", Float.class)).isEqualTo(716.0f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.distance", Float.class)).isEqualTo(22495.4f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$.time", Integer.class)).isEqualTo(5148);
  }
}
