package mucsi96.trainingLog.withings;

import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.withings.data.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class WithingsService {
    @Value("${withings.clientId}")
    String withingsClientId;

    @Value("${withings.clientSecret}")
    String withingsClientSecret;

    public String getAuthorizationCodeUrl(String state, String redirectUri) {
        return UriComponentsBuilder
                .fromHttpUrl("https://account.withings.com")
                .path("/oauth2_user/authorize2")
                .queryParam("response_type", "code")
                .queryParam("client_id", withingsClientId)
                .queryParam("state", state)
                .queryParam("scope", "user.metrics")
                .queryParam("redirect_uri", redirectUri)
                .build()
                .encode()
                .toUriString();
    }

    public HttpEntity<MultiValueMap<String, String>> getAccessTokenRequest(String authorizationCode, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("action", "requesttoken");
        body.add("client_id", withingsClientId);
        body.add("client_secret", withingsClientSecret);
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);
        body.add("redirect_uri", redirectUri);
        return new HttpEntity<>(body, headers);
    }

    public HttpEntity<MultiValueMap<String, String>> getRefreshAccessTokenRequest(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("action", "requesttoken");
        body.add("client_id", withingsClientId);
        body.add("client_secret", withingsClientSecret);
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);
        return new HttpEntity<>(body, headers);
    }

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

    public GetAccessTokenResponse getAccessToken(String authorizationCode, String redirectUri) {
        RestTemplate restTemplate = new RestTemplate();
        GetAccessTokenResponse response = restTemplate.postForObject(
                "https://wbsapi.withings.net/v2/oauth2",
                getAccessTokenRequest(authorizationCode, redirectUri),
                GetAccessTokenResponse.class
        );
        return response;
    }

    public GetAccessTokenResponse refreshAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(
                "https://wbsapi.withings.net/v2/oauth2",
                getRefreshAccessTokenRequest(refreshToken),
                GetAccessTokenResponse.class
        );
    }

    public GetMeasureResponseBody getMeasure(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        RestTemplate restTemplate = new RestTemplate();
        GetMeasureResponse response = restTemplate
                .postForObject(getMeasureUrl(), request, GetMeasureResponse.class);
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
