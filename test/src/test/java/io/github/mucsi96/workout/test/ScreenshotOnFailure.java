package io.github.mucsi96.workout.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ScreenshotOnFailure implements AfterEachCallback {

  WebDriver webDriver;

  static {
    try {
      FileUtils.deleteDirectory(new File("screenshots"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    if (context.getExecutionException().isPresent()) {
      String name = context.getTestClass().get().getSimpleName() + "-"
          + context.getDisplayName().replaceAll("[^a-zA-Z0-9_]", "");
      ApplicationContext springContext = SpringExtension
          .getApplicationContext(context);
      BaseIntegrationTest.takeScreenshot(springContext.getBean(WebDriver.class), name);
    }
  }


}
