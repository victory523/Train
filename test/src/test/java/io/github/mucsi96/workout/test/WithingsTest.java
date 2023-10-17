package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class WithingsTest extends BaseIntegrationTest {

  @Test
  void authorizes_withings() {
    setupMocks();
    jdbcTemplate.execute("DELETE FROM oauth2_authorized_client WHERE client_registration_id = 'withings-client';");
    webDriver.get(baseUrl);
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h1[contains(text(), \"Mock Withings\")]")));
    webDriver.findElement(By.xpath("//a[contains(text(), \"Authorize\")]")).click();
    longWait.until(ExpectedConditions.urlToBe(baseUrl + "/week"));
    String userName = jdbcTemplate.queryForObject("SELECT principal_name FROM oauth2_authorized_client WHERE client_registration_id = 'withings-client';", String.class);
    assertThat(userName).isEqualTo("rob");
  }

  @Test
  void pulls_todays_weigth_measurements_from_withings_to_db() {
    setupMocks();
    open();
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.xpath("//h2[contains(text(),\"Weight\")]")));
    WebElement weighElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Weight\")]/following-sibling::*"));
    assertThat(weighElement.getText()).isEqualTo("87.2 kg");
    WebElement bodyFatElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Body fat\")]/following-sibling::*"));
    assertThat(bodyFatElement.getText()).isEqualTo("21.8 kg");
    WebElement fatRatioElement = webDriver
        .findElement(By.xpath("//h2[contains(text(), \"Fat ratio\")]/following-sibling::*"));
    assertThat(fatRatioElement.getText()).isEqualTo("35.3 %");
  }
}
