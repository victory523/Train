package mucsi96.traininglog.strava;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v113.network.Network;
import org.openqa.selenium.devtools.v113.network.model.RequestId;
import org.openqa.selenium.devtools.v113.network.model.ResourceType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mucsi96.traininglog.fitness.Fitness;

@Service
@RequiredArgsConstructor
@Slf4j
public class StravaFintnessService {
  private final WebDriver webDriver;
  private final StravaConfiguration configuration;

  private Optional<StravaFitnessProfile> getTodayFitnessProfile(String responseBody) {

    ObjectMapper mapper = new ObjectMapper();
    List<StravaFitnessResponse> response;
    try {
      response = mapper.readValue(responseBody, new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      log.error("Cannot parse fitness response", e);
      return Optional.empty();
    }
    List<StravaFitnessData> dataList = response.get(0).getData();
    return dataList.stream().filter(data -> {
      OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
      int year = data.getDate().getYear();
      int month = data.getDate().getMonth();
      int day = data.getDate().getDay();
      return year == now.getYear() && month == now.getMonthValue() && day == now.getDayOfMonth();
    }).findFirst().map(StravaFitnessData::getFitnessProfile);
  }

  public Optional<Fitness> getFitnessLevel() {
    log.info("Getting fitenss");
    webDriver.manage().deleteAllCookies();
    webDriver.get(configuration.getApiUri() + "/athlete/fitness");
    WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.id("email")));
    DevTools devTools = ((HasDevTools) webDriver).getDevTools();
    devTools.createSession();
    List<RequestId> matches = new ArrayList<>();
    devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));
    devTools.addListener(Network.requestWillBeSent(), request -> {
      request.getType().ifPresent((value) -> {
        if (value == ResourceType.XHR && request.getRequest().getUrl().matches(".*\\/fitness\\/\\d+\\?.*")) {
          matches.add(request.getRequestId());
        }
      });
    });
    webDriver.findElement(By.id("email")).sendKeys(configuration.getUsername());
    webDriver.findElement(By.id("password")).sendKeys(configuration.getPassword());
    webDriver.findElement(By.id("login-button")).click();
    wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.cssSelector(".fitness-dot")));
    log.info("Successful login");
    if (matches.size() == 0) {
      log.error("No matching fitenss request");
      return Optional.empty();
    }
    String body = devTools.send(Network.getResponseBody(matches.get(0))).getBody();
    Optional<StravaFitnessProfile> profile = getTodayFitnessProfile(body);
    devTools.disconnectSession();
    webDriver.manage().deleteAllCookies();
    profile.ifPresent(value -> {
      ObjectMapper mapper = new ObjectMapper();
      try {
        System.out.println(mapper.writeValueAsString(value));
      } catch (JsonProcessingException e) {
      }
    });
    return profile.map(
        value -> Fitness.builder()
            .createdAt(ZonedDateTime.now(ZoneOffset.UTC))
            .fitness(value.getFitness())
            .fatigue(value.getFatigue())
            .form(value.getForm())
            .build());
  }
}
