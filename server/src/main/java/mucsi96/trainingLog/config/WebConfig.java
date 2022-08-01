package mucsi96.trainingLog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.web")
@Data
public class WebConfig {
    private String publicUrl;
    private String publicApiUrl;
}
