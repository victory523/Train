package io.github.mucsi96.workout.test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@ExtendWith(ScreenshotOnFailure.class)
public class BaseIntegrationTest {

  @Autowired
  WebDriver webDriver;

  @Autowired
  String baseUrl;

  @Autowired
  JdbcTemplate jdbcTemplate;

  WebDriverWait wait;

  public void setupMocks(Runnable prepare) {
    wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
    cleanupDB();
    // initDB();
    prepare.run();
    webDriver.get(baseUrl);
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.tagName("app-header")));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.tagName("app-loader")));
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.tagName("app-header")));
  }

  public void setupMocks() {
    setupMocks(() -> {
    });
  }

  public void cleanupDB() {
    List<String> tables = jdbcTemplate.queryForList(
        "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")
        .stream().map(table -> (String) table.get("table_name")).toList();

    tables.stream().forEach(table -> jdbcTemplate
        .execute(String.format("DELETE FROM \"%s\";", table)));
  }

  public static void takeScreenshot(WebDriver webDriver, String name) {
    File tmpFile = ((TakesScreenshot) webDriver)
        .getScreenshotAs(OutputType.FILE);
    File destFile = new File("screenshots/" + name + ".png");
    try {
      if (destFile.exists()) {
        destFile.delete();
      }
      FileUtils.moveFile(tmpFile, destFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
