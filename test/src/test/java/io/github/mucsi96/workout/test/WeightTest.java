package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import lombok.Data;

class WeightTest extends BaseIntegrationTest {

  @Data
  static class Weight {
    private Instant createdAt;
    private double value;
  }

  @Test
  void display_todays_weight_from_db() {
    setupMocks();
    jdbcTemplate.execute("DELETE FROM weight;");
    jdbcTemplate
        .execute(String.format("INSERT INTO weight (created_at, value) VALUES ('%s', 95.34 );",
            Timestamp.from(Instant.now())));
    webDriver.navigate().refresh();
    waitForLoad();
    WebElement element = webDriver
        .findElement(By.xpath("//app-heading[contains(text(), \"Weight\")]"));
    assertThat(element.getText()).isEqualToIgnoringWhitespace("Weight 95.34");
  }

}
