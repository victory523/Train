package mucsi96.trainingLog.withings;

import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.oauth.UnauthorizedClientException;
import mucsi96.trainingLog.withings.data.GetMeasureResponse;
import mucsi96.trainingLog.withings.data.GetMeasureResponseBody;
import mucsi96.trainingLog.withings.data.Measure;
import mucsi96.trainingLog.withings.data.MeasureGroup;
import mucsi96.trainingLog.withings.oauth.WithingsClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
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

    public String getMeasureUrl() {
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

    public GetMeasureResponseBody getMeasure(OAuth2AuthorizedClient authorizedClient) {
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
            throw new UnauthorizedClientException(WithingsClient.id);
        }

        if (response.getStatus() != 0) {
            throw new WithingsTechnicalException();
        }

        return response.getBody();
    }

    public Double getFirstMeasureValue(GetMeasureResponseBody measureResponseBody) {
        List<MeasureGroup> measureGroups = measureResponseBody.getMeasureGroups();

        if (measureGroups == null || measureGroups.isEmpty()) {
            return null;
        }

        List<Measure> measures = measureGroups.get(0).getMeasures();

        if (measures == null || measures.isEmpty()) {
            return null;
        }

        Measure measure = measures.get(0);

        return measure.getValue() *  Math.pow(10, measure.getUnit());
    }
}
