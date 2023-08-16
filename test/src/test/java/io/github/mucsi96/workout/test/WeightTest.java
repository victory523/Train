package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import lombok.Data;

class WeightTest extends BaseIntegrationTest {

  @Data
  static class Weight {
    private Instant createdAt;
    private double value;
  }

  @BeforeEach
  void beforeEach() {
    setupMocks();
    jdbcTemplate.execute("DELETE FROM weight;");
    jdbcTemplate.execute(String.format("INSERT INTO weight (created_at, value) VALUES ('%s', 108.9);",
        Timestamp.from(Instant.now().minus(400, ChronoUnit.DAYS))));
    jdbcTemplate.execute(String.format("INSERT INTO weight (created_at, value) VALUES ('%s', 98);",
        Timestamp.from(Instant.now().minus(356, ChronoUnit.DAYS))));
    jdbcTemplate.execute(String.format("INSERT INTO weight (created_at, value) VALUES ('%s', 89.4);",
        Timestamp.from(Instant.now().minus(14, ChronoUnit.DAYS))));
    jdbcTemplate.execute(String.format("INSERT INTO weight (created_at, value) VALUES ('%s', 88.3);",
        Timestamp.from(Instant.now().minus(6, ChronoUnit.DAYS))));
    jdbcTemplate.execute(String.format("INSERT INTO weight (created_at, value) VALUES ('%s', 87.7);",
        Timestamp.from(Instant.now().minus(5, ChronoUnit.DAYS))));
    jdbcTemplate.execute(String.format("INSERT INTO weight (created_at, value) VALUES ('%s', 87.5);",
        Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS))));
  }

  @Test
  void display_todays_weight() {
    open();
    WebElement element = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Weight\")]"));
    assertThat(element.getText()).isEqualToIgnoringWhitespace("Weight 87.15");
  }

  @Test
  void display_weight_chart_for_week() {
    open();
    WebElement chart = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Weight\")]/following-sibling::*[@role=\"img\"]"));
    wait.until(ExpectedConditions.attributeContains(chart, "aria-label", "This is a chart with type Line chart."));
    String label = chart.getAttribute("aria-label");
    assertThat(label).contains("The data is as follows:", "88.3,", "87.7,", "87.5,", "87.15.");
    assertThat(label).doesNotContain("108.9,", "98,", "89.4,");
  }

  @Test
  void display_weight_chart_for_month() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//h2[contains(text(), \"Weight\")]/parent::*//button[contains(text(), \"Month\")]"));

    button.click();

    waitForLoad();

    WebElement chart = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Weight\")]/following-sibling::*[@role=\"img\"]"));
    wait.until(ExpectedConditions.attributeContains(chart, "aria-label", "This is a chart with type Line chart."));
    String label = chart.getAttribute("aria-label");
    assertThat(label).contains("The data is as follows:", "89.4,", "88.3,", "87.7,", "87.5,", "87.15.");
    assertThat(label).doesNotContain("108.9,", "98,");
  }

  @Test
  void display_weight_chart_for_year() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//h2[contains(text(), \"Weight\")]/parent::*//button[contains(text(), \"Year\")]"));

    button.click();

    waitForLoad();

    WebElement chart = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Weight\")]/following-sibling::*[@role=\"img\"]"));
    wait.until(ExpectedConditions.attributeContains(chart, "aria-label", "This is a chart with type Line chart."));
    String label = chart.getAttribute("aria-label");
    assertThat(label).contains("The data is as follows:", "98,", "89.4,", "88.3,", "87.7,", "87.5,", "87.15.");
    assertThat(label).doesNotContain("108.9,");
  }

  @Test
  void display_weight_chart_for_all_time() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//h2[contains(text(), \"Weight\")]/parent::*//button[contains(text(), \"All time\")]"));

    button.click();

    waitForLoad();

    WebElement chart = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Weight\")]/following-sibling::*[@role=\"img\"]"));
    wait.until(ExpectedConditions.attributeContains(chart, "aria-label", "This is a chart with type Line chart."));
    String label = chart.getAttribute("aria-label");
    assertThat(label).contains("The data is as follows:", "108.9,", "98,", "89.4,", "88.3,", "87.7,", "87.5,", "87.15.");
  }

}
