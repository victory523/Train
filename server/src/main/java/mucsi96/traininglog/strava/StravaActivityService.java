package mucsi96.traininglog.strava;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mucsi96.traininglog.activity.Activity;

@Service
@RequiredArgsConstructor
@Slf4j
public class StravaActivityService {
  private final StravaConfiguration configuration;
  private final Clock clock;

  private String getActivitiesUrl(ZoneId zoneId) {
    long startTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS).minusDays(1)
        .toEpochSecond();
    long endTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS).plusDays(1)
        .toEpochSecond();
    log.info("Getting today activities from {} to {}", startTime, endTime);
    return UriComponentsBuilder
        .fromHttpUrl(configuration.getApiUri())
        .path("/api/v3/athlete/activities")
        .queryParam("after", startTime)
        .queryParam("before", endTime)
        .build()
        .encode()
        .toUriString();
  }

  private List<Long> getTodayActivityIds(OAuth2AuthorizedClient authorizedClient, ZoneId zoneId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
    HttpEntity<String> request = new HttpEntity<>("", headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<List<StravaSummaryActivity>> response = restTemplate.exchange(getActivitiesUrl(zoneId),
        HttpMethod.GET, request,
        new ParameterizedTypeReference<List<StravaSummaryActivity>>() {
        }, headers);
    List<StravaSummaryActivity> activities = response.getBody();

    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      System.out.println(mapper.writeValueAsString(activities));
    } catch (JsonProcessingException e) {
    }

    if (response.getStatusCode() == HttpStatusCode.valueOf(401)) {
      throw new ClientAuthorizationRequiredException(StravaConfiguration.registrationId);
    }

    if (response.getStatusCode() != HttpStatusCode.valueOf(200)) {
      throw new StravaTechnicalException();
    }

    return activities.stream().map(StravaSummaryActivity::getId).toList();
  }

  public List<Activity> getTodayActivities(OAuth2AuthorizedClient authorizedClient, ZoneId zoneId) {
    return getTodayActivityIds(authorizedClient, zoneId).stream().map(id -> {
      log.info("Getting Strava activity with id" + id);
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
      HttpEntity<String> request = new HttpEntity<>("", headers);
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<StravaDetailedActivity> response = restTemplate.exchange(
          configuration.getApiUri() + "/api/v3/activities/" + id,
          HttpMethod.GET, request,
          StravaDetailedActivity.class, headers);
      StravaDetailedActivity activity = response.getBody();

      if (response.getStatusCode() == HttpStatusCode.valueOf(401)) {
        throw new ClientAuthorizationRequiredException(StravaConfiguration.registrationId);
      }

      if (response.getStatusCode() != HttpStatusCode.valueOf(200)) {
        throw new StravaTechnicalException();
      }

      try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        System.out.println(mapper.writeValueAsString(activity));
      } catch (JsonProcessingException e) {
      }

      return Activity.builder()
          .createdAt(activity.getStartDate().atZoneSameInstant(ZoneOffset.UTC))
          .name(activity.getName())
          .movingTime(activity.getMovingTime())
          .distance(activity.getDistance())
          .totalElevationGain(activity.getTotalElevationGain())
          .weightedAverageWatts(activity.getWeightedAverageWatts())
          .calories(activity.getCalories())
          .sportType(activity.getSportType())
          .build();
    }).toList();
  }

}
