package mucsi96.traininglog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

class DevContainerNetwork implements Network {

  @Override
  public String getId() {
    return System.getenv("DOCKER_NETWORK");
  }

  @Override
  public void close() {
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return null;
  }

};

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BaseIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  static PostgreSQLContainer<?> dbMock;

  HttpHeaders getAuthHeaders(String authority) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Remote-User", "rob");
    headers.add("Remote-Groups", authority);
    headers.add("Remote-Name", "Robert White");
    headers.add("Remote-Email", "robert.white@mockemail.com");
    return headers;
  }

  @BeforeAll
  public static void setUp() {
    if (dbMock != null) {
      return;
    }

    dbMock = new PostgreSQLContainer<>("postgres:15.3-alpine3.18");

    if (System.getenv("DOCKER_NETWORK") != null) {
      Network network = new DevContainerNetwork();
      dbMock.withNetwork(network);
    }

    dbMock.start();
  }

  @DynamicPropertySource
  public static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", dbMock::getJdbcUrl);
    registry.add("spring.datasource.username", dbMock::getUsername);
    registry.add("spring.datasource.password", dbMock::getPassword);
  }
}
