package io.github.mucsi96.workout.test;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

  public void setupMocksWithNoAuth() {
    wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
    cleanupDB();
  }

  public void setupMocks() {
    setupMocksWithNoAuth();
    jdbcTemplate.execute("INSERT INTO oauth2_authorized_client (" + //
        "client_registration_id," + //
        "principal_name," + //
        "access_token_type," + //
        "access_token_value," + //
        "access_token_issued_at," + //
        "access_token_expires_at," + //
        "access_token_scopes," + //
        "refresh_token_value," + //
        "refresh_token_issued_at," + //
        "created_at" + //
        ") VALUES (" + //
        "'withings-client'," + //
        "'rob'," + //
        "'Bearer'," + //
        "'test-access-token'," + //
        String.format("'%s',", Timestamp.from(Instant.now())) + //
        String.format("'%s',", Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS))) + //
        "'user.metrics'," + //
        "'test-refresh-token'," + //
        String.format("'%s',", Timestamp.from(Instant.now())) + //
        String.format("'%s'", Timestamp.from(Instant.now())) + //
        ");");
    jdbcTemplate.execute("INSERT INTO oauth2_authorized_client (" + //
        "client_registration_id," + //
        "principal_name," + //
        "access_token_type," + //
        "access_token_value," + //
        "access_token_issued_at," + //
        "access_token_expires_at," + //
        "access_token_scopes," + //
        "refresh_token_value," + //
        "refresh_token_issued_at," + //
        "created_at" + //
        ") VALUES (" + //
        "'strava-client'," + //
        "'rob'," + //
        "'Bearer'," + //
        "'test-access-token'," + //
        String.format("'%s',", Timestamp.from(Instant.now())) + //
        String.format("'%s',", Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS))) + //
        "'activity:read'," + //
        "'test-refresh-token'," + //
        String.format("'%s',", Timestamp.from(Instant.now())) + //
        String.format("'%s'", Timestamp.from(Instant.now())) + //
        ");");
  }

  public void waitForLoad() {
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.tagName("article")));
  }

  public void open() {
    webDriver.get(baseUrl);
    waitForLoad();
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
