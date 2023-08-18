package mucsi96.traininglog.core;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TimeConfiguration {
  @Bean
  @Profile({ "prod", "local" })
  public Clock clock() {
    return Clock.systemUTC();
  }
}
