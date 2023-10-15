package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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

  // @Test
  // void display_todays_weight() {
  // open();
  // WebElement weighElement = webDriver
  // .findElement(By.xpath("//h2[contains(text(),
  // \"Weight\")]/following-sibling::*"));
  // assertThat(weighElement.getText()).isEqualToIgnoringWhitespace("87.2 kg");
  // WebElement bodyFatElement = webDriver
  // .findElement(By.xpath("//h2[contains(text(), \"Body
  // fat\")]/following-sibling::*"));
  // assertThat(bodyFatElement.getText()).isEqualToIgnoringWhitespace("21.8 kg");
  // WebElement fatRatioElement = webDriver
  // .findElement(By.xpath("//h2[contains(text(), \"Fat
  // ratio\")]/following-sibling::*"));
  // assertThat(fatRatioElement.getText()).isEqualToIgnoringWhitespace("35.3 %");
  // }

  @Test
  void display_ride_stats_for_week() {
    open();
    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualToIgnoringWhitespace("1 938");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualToIgnoringWhitespace("1 224 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualToIgnoringWhitespace("35 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualToIgnoringWhitespace("2 h 34 min");
  }

  @Test
  void display_ride_stats_for_month() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//a[contains(text(), \"Month\")]"));
    button.click();
    waitForLoad();

    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualToIgnoringWhitespace("2 584");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualToIgnoringWhitespace("1 632 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualToIgnoringWhitespace("47 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualToIgnoringWhitespace("3 h 25 min");
  }

  @Test
  void display_ride_stats_for_year() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//a[contains(text(), \"Year\")]"));
    button.click();
    waitForLoad();

    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualToIgnoringWhitespace("3 230");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualToIgnoringWhitespace("2 040 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualToIgnoringWhitespace("59 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualToIgnoringWhitespace("4 h 16 min");
  }

  @Test
  void display_ride_stats_for_all_time() {
    open();
    WebElement button = webDriver
        .findElement(
            By.xpath("//a[contains(text(), \"All time\")]"));
    button.click();
    waitForLoad();

    WebElement caloriesElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Calories\")]/following-sibling::*/following-sibling::*"));
    assertThat(caloriesElement.getText()).isEqualToIgnoringWhitespace("4746");
    WebElement elevationGainElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Elevation gain\")]/following-sibling::*/following-sibling::*"));
    assertThat(elevationGainElement.getText()).isEqualToIgnoringWhitespace("2 964 m");
    WebElement distanceElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Distance\")]/following-sibling::*/following-sibling::*"));
    assertThat(distanceElement.getText()).isEqualToIgnoringWhitespace("99 km");
    WebElement timeElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Time\")]/following-sibling::*/following-sibling::*"));
    assertThat(timeElement.getText()).isEqualToIgnoringWhitespace("6 h 18 min");
  }

}
