package mucsi96.traininglog.withings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "withings")
public class WithingsConfiguration {
  private WithingsApiConfiguration api;

  @Data
  public static class WithingsApiConfiguration {
    private String uri;
  }
}
