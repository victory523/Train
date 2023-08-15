package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

class BackupTest extends BaseIntegrationTest {

  @Test
  void displays_last_backup_time() {
    setupMocks();
    open();
    WebElement element = webDriver
        .findElement(By.xpath("//header//*[@role=\"status\"]"));
    assertThat(element.getText()).isEqualToIgnoringWhitespace("Last backup 5 minutes ago");
  }

}
