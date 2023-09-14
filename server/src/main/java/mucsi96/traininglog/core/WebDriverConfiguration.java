package mucsi96.traininglog.core;

import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "webdriver")
public class WebDriverConfiguration {
  private String apiUri;

  @Bean
  public WebDriver getWebDriver() {
  ChromeOptions options = new ChromeOptions().addArguments("--headless",
  "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage",
  "--window-size=1920,1080", "--remote-allow-origins=*");

  ChromeDriver driver = new ChromeDriver(options);
  driver.setLogLevel(Level.WARNING);
  return driver;
  }

  // @Bean
  // public WebDriver getWebDriver() throws MalformedURLException {
  //   ChromeOptions options = new ChromeOptions().addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

  //   RemoteWebDriver driver = new RemoteWebDriver(new URL(apiUri), options);
  //   driver.setLogLevel(Level.WARNING);
  //   return new Augmenter().augment(driver);
  // }
}
