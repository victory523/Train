package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class StravaTest extends BaseIntegrationTest {

  @Test
  void authorizes_strava() {
    setupMocks();
    jdbcTemplate.execute("DELETE FROM oauth2_authorized_client WHERE client_registration_id = 'strava-client';");
    webDriver.get(baseUrl);
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h1[contains(text(), \"Mock Strava\")]")));
    webDriver.findElement(By.xpath("//a[contains(text(), \"Authorize\")]")).click();
    longWait.until(ExpectedConditions.urlToBe(baseUrl + "/week"));
    String userName = jdbcTemplate.queryForObject(
        "SELECT principal_name FROM oauth2_authorized_client WHERE client_registration_id = 'strava-client';",
        String.class);
    assertThat(userName).isEqualTo("rob");
  }

  @Test
  void pulls_todays_ride_stats_from_strava_to_db() {
    setupMocks();
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
}
