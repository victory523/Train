package mucsi96.traininglog.withings;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mucsi96.traininglog.weight.Weight;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithingsService {

  private final WithingsConfiguration withingsConfiguration;
  private final Clock clock;

  private String getMeasureUrl(ZoneId zoneId) {
    long startTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS).toEpochSecond();
    long endTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS).plusDays(1)
        .toEpochSecond();
    log.info("Getting today last weight measure from {} to {}", startTime, endTime);
    return UriComponentsBuilder
        .fromHttpUrl(withingsConfiguration.getApi().getUri())
        .path("/measure")
        .queryParam("action", "getmeas")
        .queryParam("category", 1)
        .queryParam("startdate", startTime)
        .queryParam("enddate", endTime)
        .build()
        .encode()
        .toUriString();
  }

  private WithingsGetMeasureResponseBody getMeasure(OAuth2AuthorizedClient authorizedClient, ZoneId zoneId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
    HttpEntity<String> request = new HttpEntity<>("", headers);
    RestTemplate restTemplate = new RestTemplate();
    WithingsGetMeasureResponse response = restTemplate
        .postForObject(getMeasureUrl(zoneId), request, WithingsGetMeasureResponse.class);

    if (response == null) {
      throw new WithingsTechnicalException();
    }

    if (response.getStatus() == 401) {
      throw new ClientAuthorizationRequiredException(WithingsConfiguration.registrationId);
    }

    if (response.getStatus() != 0) {
      log.error(response.getError());
      throw new WithingsTechnicalException();
    }

    return response.getBody();
  }

  private double getMeasurement(List<WithingsMeasure> measures, int type) {
    WithingsMeasure measure = measures.stream().filter(m -> m.getType() == type).findFirst().orElseThrow();
    double weight = Math.round(measure.getValue() * Math.pow(10, measure.getUnit()) * 10.0) / 10.0;
    return weight;
  }

  private Optional<Weight> getLastMeasureValue(WithingsGetMeasureResponseBody measureResponseBody) {
    List<WithingsMeasureGroup> measureGroups = measureResponseBody.getMeasuregrps();

    if (measureGroups == null || measureGroups.isEmpty()) {
      return Optional.empty();
    }

    WithingsMeasureGroup measureGroup = measureGroups.get(measureGroups.size() - 1);

    List<WithingsMeasure> measures = measureGroup.getMeasures();

    if (measures == null || measures.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(
        Weight
            .builder()
            .createdAt(ZonedDateTime.ofInstant(Instant.ofEpochSecond(measureGroup.getDate()), ZoneOffset.UTC))
            .weight(getMeasurement(measures, 1))
            .fatRatio(getMeasurement(measures, 6))
            .fatMassWeight(getMeasurement(measures, 8))
            .build());
  }

  public Optional<Weight> getTodayWeight(OAuth2AuthorizedClient authorizedClient, ZoneId zoneId) {
    Optional<Weight> result = getLastMeasureValue(getMeasure(authorizedClient, zoneId));
    log.info("Got {}", result.isPresent() ? result.get().getWeight() : "null");
    return result;
  }
}
