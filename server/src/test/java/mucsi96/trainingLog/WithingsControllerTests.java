package mucsi96.trainingLog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class WithingsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void returnsUnauthorizedWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/withings/weight"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$._links.oauth2Login.href")
                        .value("/api/oauth2/authorization/withings-client")
                );
    }
}
