package mucsi96.traininglog.withings;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
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
import mucsi96.traininglog.weight.Weight;
import mucsi96.traininglog.withings.data.GetMeasureResponse;
import mucsi96.traininglog.withings.data.GetMeasureResponseBody;
import mucsi96.traininglog.withings.data.Measure;
import mucsi96.traininglog.withings.data.MeasureGroup;
import mucsi96.traininglog.withings.oauth.WithingsClient;

@Service
@RequiredArgsConstructor
public class WithingsService {

  private final WithingsConfiguration withingsConfiguration;

  private String getMeasureUrl() {
    long startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.UTC).getEpochSecond();
    long endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toInstant(ZoneOffset.UTC).getEpochSecond();
    return UriComponentsBuilder
        .fromHttpUrl(withingsConfiguration.getApi().getUri())
        .path("/measure")
        .queryParam("action", "getmeas")
        .queryParam("meastype", 1)
        .queryParam("category", 1)
        .queryParam("startdate", startTime)
        .queryParam("enddate", endTime)
        .build()
        .encode()
        .toUriString();
  }

  private GetMeasureResponseBody getMeasure(OAuth2AuthorizedClient authorizedClient) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
    HttpEntity<String> request = new HttpEntity<>("", headers);
    RestTemplate restTemplate = new RestTemplate();
    GetMeasureResponse response = restTemplate
        .postForObject(getMeasureUrl(), request, GetMeasureResponse.class);

    if (response == null) {
      throw new WithingsTechnicalException();
    }

    if (response.getStatus() == 401) {
      throw new ClientAuthorizationRequiredException(WithingsClient.id);
    }

    if (response.getStatus() != 0) {
      throw new WithingsTechnicalException();
    }

    return response.getBody();
  }

  private Optional<Weight> getFirstMeasureValue(GetMeasureResponseBody measureResponseBody) {
    List<MeasureGroup> measureGroups = measureResponseBody.getMeasureGroups();

    if (measureGroups == null || measureGroups.isEmpty()) {
      return Optional.empty();
    }

    MeasureGroup measureGroup = measureGroups.get(0);

    List<Measure> measures = measureGroup.getMeasures();

    if (measures == null || measures.isEmpty()) {
      return Optional.empty();
    }

    Measure measure = measures.get(0);
    double weight = measure.getValue() * Math.pow(10, measure.getUnit());

    return Optional.of(Weight.builder().value(weight).createdAt(Instant.ofEpochSecond(measureGroup.getDate())).build());
  }

  public Optional<Weight> getTodayWeight(OAuth2AuthorizedClient authorizedClient) {
    return getFirstMeasureValue(getMeasure(authorizedClient));
  }
}
