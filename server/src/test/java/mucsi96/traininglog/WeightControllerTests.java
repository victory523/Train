package mucsi96.traininglog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
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

  @AfterEach
  void afterEach() {
    weightRepository.deleteAll();
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
                .headers(getAuthHeaders("guest")))
        .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  public void returns_weight_from_database() throws Exception {
    Weight weight = Weight.builder()
        .value(83.5)
        .createdAt(Instant.now())
        .build();
    weightRepository.save(weight);

    MockHttpServletResponse response = mockMvc
        .perform(
            get("/weight")
                .headers(getAuthHeaders("user")))
        .andReturn().getResponse();

    assertThat(JsonPath.parse(response.getContentAsString()).read("$.weight", Double.class)).isEqualTo(83.5);
  }
}
