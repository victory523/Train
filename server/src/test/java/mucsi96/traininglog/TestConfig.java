package mucsi96.traininglog;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
  @Bean
  public Clock clock() {
    return Clock.fixed(
        Instant.parse("2031-08-22T10:00:00Z"),
        ZoneOffset.UTC);
  }
}
