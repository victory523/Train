package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import lombok.Data;

public class WithingsTest extends BaseIntegrationTest {

  @Data
  static class Weight {
    private Instant createdAt;
    private double value;
  }

  @Test
  void pulls_todays_weigth_from_withings_to_db() {
    setupMocks();
    List<Weight> weights = jdbcTemplate
        .queryForStream("SELECT * FROM weight;", new BeanPropertyRowMapper<>(Weight.class)).toList();
    assertThat(weights).size().isEqualTo(1);
    assertThat(weights.get(0).value).isEqualTo(65.75);
    Instant startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.UTC);
    Instant endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toInstant(ZoneOffset.UTC);
    assertThat(weights.get(0).createdAt).isBetween(startTime, endTime);
  }
}
