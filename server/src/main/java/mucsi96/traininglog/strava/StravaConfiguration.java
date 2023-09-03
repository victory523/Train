package mucsi96.traininglog.strava;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "strava")
public class StravaConfiguration {
  private String username;
  private String password;

}
