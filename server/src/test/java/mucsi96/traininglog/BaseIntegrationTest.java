package mucsi96.traininglog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    HttpHeaders getAuthHeaders(String authority) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Remote-User", "rob");
        headers.add("Remote-Groups", authority);
        headers.add("Remote-Name", "Robert White");
        headers.add("Remote-Email", "robert.white@mockemail.com");
        return headers;
    }
}
