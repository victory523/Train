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
import mucsi96.traininglog.weight.Weight;
import mucsi96.traininglog.weight.WeightRepository;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class WeightControllerTests extends BaseIntegrationTest {

  private final MockMvc mockMvc;
  private final WeightRepository weightRepository;
  private final Clock clock;

  @BeforeEach
  void beforeEach() {
    weightRepository.deleteAll();
    weightRepository.save(Weight.builder()
        .weight(108.9)
        .createdAt(ZonedDateTime.now(clock).minus(400, ChronoUnit.DAYS))
        .build());
    weightRepository.save(Weight.builder()
        .weight(98)
        .createdAt(ZonedDateTime.now(clock).minus(356, ChronoUnit.DAYS))
        .build());
    weightRepository.save(Weight.builder()
        .weight(88.3)
        .createdAt(ZonedDateTime.now(clock).minus(6, ChronoUnit.DAYS))
        .build());
    weightRepository.save(Weight.builder()
        .weight(87.7)
        .createdAt(ZonedDateTime.now(clock).minus(5, ChronoUnit.DAYS))
        .build());
    weightRepository.save(Weight.builder()
        .weight(87.1)
        .fatRatio(31.01)
        .fatMassWeight(21.34)
        .createdAt(ZonedDateTime.now(clock))
        .build());
    weightRepository.save(Weight.builder()
        .weight(87.5)
        .createdAt(ZonedDateTime.now(clock).minus(1, ChronoUnit.DAYS))
        .build());
  }

  @Test
  public void returns_not_authorized_if_no_preauth_headers_are_sent() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight"))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  public void returns_forbidden_if_user_has_no_user_role() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .headers(getHeaders("guest")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void returns_today_weight_measurement() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .queryParam("period", "1")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.length()", Integer.class)).isEqualTo(1);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].date", String.class))
        .isEqualTo("2031-08-22T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].weight", Double.class)).isEqualTo(87.1);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].fatRatio", Double.class)).isEqualTo(31.01);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].fatMassWeight", Double.class)).isEqualTo(21.34);
  }

  @Test
  public void returns_one_week_weight_measurements() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .queryParam("period", "7")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.length()", Integer.class)).isEqualTo(4);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].weight", Double.class)).isEqualTo(88.3);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].date", String.class))
        .isEqualTo("2031-08-16T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[1].weight", Double.class)).isEqualTo(87.7);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[1].date", String.class))
        .isEqualTo("2031-08-17T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[2].weight", Double.class)).isEqualTo(87.5);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[2].date", String.class))
        .isEqualTo("2031-08-21T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[3].weight", Double.class)).isEqualTo(87.1);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[3].date", String.class))
        .isEqualTo("2031-08-22T06:00:00-04:00");
  }

  @Test
  public void returns_all_time_weight_measurements() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .headers(getHeaders("user")))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.length()", Integer.class)).isEqualTo(6);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].weight", Double.class)).isEqualTo(108.9);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].date", String.class))
        .isEqualTo("2030-07-18T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[5].weight", Double.class)).isEqualTo(87.1);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[5].date", String.class))
        .isEqualTo("2031-08-22T06:00:00-04:00");
  }
}
