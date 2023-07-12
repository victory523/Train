package mucsi96.traininglog.withings;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import mucsi96.traininglog.weight.Weight;
import mucsi96.traininglog.withings.data.GetMeasureResponse;
import mucsi96.traininglog.withings.data.GetMeasureResponseBody;
import mucsi96.traininglog.withings.data.Measure;
import mucsi96.traininglog.withings.data.MeasureGroup;
import mucsi96.traininglog.withings.oauth.WithingsClient;

@Service
public class WithingsService {

  int getStartDate() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    return (int) (cal.getTimeInMillis() / 1000);
  }

  int getEndDate() {
    Calendar cal = Calendar.getInstance();
    return (int) (cal.getTimeInMillis() / 1000);
  }

  private String getMeasureUrl() {
    return UriComponentsBuilder
        .fromHttpUrl("https://wbsapi.withings.net")
        .path("/measure")
        .queryParam("action", "getmeas")
        .queryParam("meastype", 1)
        .queryParam("category", 1)
        .queryParam("startdate", getStartDate())
        .queryParam("enddate", getEndDate())
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
