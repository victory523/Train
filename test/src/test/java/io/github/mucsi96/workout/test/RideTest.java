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

class RideTest extends BaseIntegrationTest {

  @BeforeEach
  void beforeEach() {
    setupMocks();
    jdbcTemplate.execute("DELETE FROM ride;");
    jdbcTemplate.execute(
        String.format("""

            INSERT INTO ride
            (
              created_at,
              calories,
              distance,
              moving_time,
              name,
              sport_type,
              total_elevation_gain,
              weighted_average_watts
            ) VALUES (
              '%s',
              646,
              11747.7,
              3074,
              'Ride 1',
              'MountainBikeRide',
              408,
              210
            );

            """, Timestamp.from(Instant.now().minus(400, ChronoUnit.DAYS))));
    jdbcTemplate
        .execute(String.format("""

            INSERT INTO ride
            (
              created_at,
              calories,
              distance,
              moving_time,
              name,
              sport_type,
              total_elevation_gain,
              weighted_average_watts
            ) VALUES (
              '%s',
              646,
              11747.7,
              3074,
              'Ride 1',
              'MountainBikeRide',
              408,
              210
            );

            """, Timestamp.from(Instant.now().minus(355, ChronoUnit.DAYS))));
    jdbcTemplate.execute(
        String.format("""

            INSERT INTO ride
            (
              created_at,
              calories,
              distance,
              moving_time,
              name,
              sport_type,
              total_elevation_gain,
              weighted_average_watts
            ) VALUES (
              '%s',
              646,
              11747.7,
              3074,
              'Ride 1',
              'MountainBikeRide',
              408,
              210
            );

            """, Timestamp.from(Instant.now().minus(14, ChronoUnit.DAYS))));
    jdbcTemplate.execute(
        String.format("""

            INSERT INTO ride
            (
              created_at,
              calories,
              distance,
              moving_time,
              name,
              sport_type,
              total_elevation_gain,
              weighted_average_watts
            ) VALUES (
              '%s',
              646,
              11747.7,
              3074,
              'Ride 1',
              'MountainBikeRide',
              408,
              210
            );

            """, Timestamp.from(Instant.now().minus(6, ChronoUnit.DAYS))));
    jdbcTemplate.execute(
        String.format("""

            INSERT INTO ride
            (
              created_at,
              calories,
              distance,
              moving_time,
              name,
              sport_type,
              total_elevation_gain,
              weighted_average_watts
            ) VALUES (
              '%s',
              646,
              11747.7,
              3074,
              'Ride 1',
              'MountainBikeRide',
              408,
              210
            );

            """, Timestamp.from(Instant.now().minus(5, ChronoUnit.DAYS))));
    jdbcTemplate.execute(
        String.format("""

            INSERT INTO ride
            (
              created_at,
              calories,
              distance,
              moving_time,
              name,
              sport_type,
              total_elevation_gain,
              weighted_average_watts
            ) VALUES (
              '%s',
              646,
              11747.7,
              3074,
              'Ride 1',
              'MountainBikeRide',
              408,
              210
            );

            """, Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS))));
  }

  @Test
  void display_todays_ride_stats() {
    open();
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h2[contains(text(),\"Calories\")]")));
    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(),\"Calories\")]/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualTo("1 740");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualTo("1 032 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualTo("56 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualTo("2 h 20 min");
  }

  @Test
  void display_ride_stats_for_week() {
    open();
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h2[contains(text(),\"Calories\")]")));
    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualTo("3 678");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualTo("2 256 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualTo("91 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualTo("4 h 54 min");
  }

  @Test
  void display_ride_stats_for_month() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//a[contains(text(), \"Month\")]"));
    button.click();
    waitForLoad();
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h2[contains(text(),\"Calories\")]")));
    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualTo("4 324");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualTo("2 664 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualTo("103 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualTo("5 h 45 min");
  }

  @Test
  void display_ride_stats_for_year() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//a[contains(text(), \"Year\")]"));
    button.click();
    waitForLoad();
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h2[contains(text(),\"Calories\")]")));
    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualTo("4 970");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualTo("3 072 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualTo("115 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualTo("6 h 36 min");
  }

  @Test
  void display_ride_stats_for_all_time() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//a[contains(text(), \"All time\")]"));
    button.click();
    waitForLoad();
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h2[contains(text(),\"Calories\")]")));
    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualTo("5 616");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualTo("3 480 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualTo("127 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualTo("7 h 28 min");
  }

}
