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
        .createdAt(ZonedDateTime.now(clock).minus(400, ChronoUnit.DAYS))
        .weight(108.9f)
        .build());
    weightRepository.save(Weight.builder()
        .createdAt(ZonedDateTime.now(clock).minus(356, ChronoUnit.DAYS))
        .weight(98f)
        .build());
    weightRepository.save(Weight.builder()
        .createdAt(ZonedDateTime.now(clock).minus(6, ChronoUnit.DAYS))
        .weight(88.3f)
        .build());
    weightRepository.save(Weight.builder()
        .createdAt(ZonedDateTime.now(clock).minus(5, ChronoUnit.DAYS))
        .weight(87.7f)
        .build());
    weightRepository.save(Weight.builder()
        .createdAt(ZonedDateTime.now(clock))
        .weight(87.1f)
        .fatRatio(31.01f)
        .fatMassWeight(21.34f)
        .build());
    weightRepository.save(Weight.builder()
        .createdAt(ZonedDateTime.now(clock).minus(1, ChronoUnit.DAYS))
        .weight(87.5f)
        .build());
  }

  @Test
  public void returns_forbidden_if_user_has_no_user_role() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .headers(getHeaders()))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void returns_today_weight_measurement() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .queryParam("period", "1")
                .headers(getHeaders()))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.length()", Integer.class)).isEqualTo(1);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].date", String.class))
        .isEqualTo("2031-08-22T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].weight", Float.class)).isEqualTo(87.1f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].fatRatio", Float.class)).isEqualTo(31.01f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].fatMassWeight", Float.class)).isEqualTo(21.34f);
  }

  @Test
  public void returns_one_week_weight_measurements() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .queryParam("period", "7")
                .headers(getHeaders()))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.length()", Integer.class)).isEqualTo(4);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].weight", Float.class)).isEqualTo(88.3f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].date", String.class))
        .isEqualTo("2031-08-16T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[1].weight", Float.class)).isEqualTo(87.7f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[1].date", String.class))
        .isEqualTo("2031-08-17T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[2].weight", Float.class)).isEqualTo(87.5f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[2].date", String.class))
        .isEqualTo("2031-08-21T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[3].weight", Float.class)).isEqualTo(87.1f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[3].date", String.class))
        .isEqualTo("2031-08-22T06:00:00-04:00");
  }

  @Test
  public void returns_all_time_weight_measurements() throws Exception {
    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .headers(getHeaders()))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.length()", Integer.class)).isEqualTo(6);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].weight", Float.class)).isEqualTo(108.9f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].date", String.class))
        .isEqualTo("2030-07-18T06:00:00-04:00");
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[5].weight", Float.class)).isEqualTo(87.1f);
    assertThat(JsonPath.parse(response.getContentAsString()).read("$[5].date", String.class))
        .isEqualTo("2031-08-22T06:00:00-04:00");
  }
}
