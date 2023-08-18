package mucsi96.traininglog.core;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfiguration {
  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }
}
