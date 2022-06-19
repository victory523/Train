package mucsi96.trainingLog.withings;

import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.withings.data.GetAccessTokenResponse;
import mucsi96.trainingLog.withings.data.GetAccessTokenResponseBody;
import mucsi96.trainingLog.withings.WithingsService;
import mucsi96.trainingLog.withings.data.WeightResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

@Slf4j
@Controller
@RequestMapping("/withings")
public class WithingsController {

    static final String ACCESS_TOKEN_COOKIE = "withings_access_token";
    static final String REFRESH_TOKEN_COOKIE = "withings_refresh_token";
    static final int REFRESH_TOKEN_EXPIRATION_TIME = 30 * 24 * 60 * 60; // 30 days
    @Value("${app.publicUrl}")
    String PUBLIC_URL;
    @Value("${app.publicApiUrl}")
    String PUBLIC_API_URL;

    @Autowired
    WithingsService withingsService;

    @GetMapping("/auth")
    void auth(HttpSession session, HttpServletResponse response) throws IOException {
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        String redirectUri = PUBLIC_API_URL + "/withings/redirect";
        session.setAttribute("state", state);
        session.setAttribute("redirectUri", redirectUri);
        response.sendRedirect(
                withingsService.getAuthorizationCodeUrl(state, redirectUri)
        );
    }

    @GetMapping("/redirect")
    void redirect(
            @RequestParam("code") String authorizationCode,
            @RequestParam("state") String stateInParam,
            @SessionAttribute("state") String stateInSession,
            @SessionAttribute("redirectUri") String redirectUri,
            HttpServletResponse response
    ) throws IOException {
        if (!stateInParam.equals(stateInSession)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        GetAccessTokenResponse accessTokenResponse = withingsService.getAccessToken(authorizationCode, redirectUri);

        if (accessTokenResponse.getStatus() != 0) {
            log.error("Non-zero status from authentication. Status: {}", accessTokenResponse.getStatus());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        GetAccessTokenResponseBody accessTokenResponseBody = accessTokenResponse.getBody();

        response.addCookie(getSecureCookie(
                ACCESS_TOKEN_COOKIE,
                accessTokenResponseBody.getAccessToken(),
                accessTokenResponseBody.getExpiresIn()
        ));
        response.addCookie(getSecureCookie(
                REFRESH_TOKEN_COOKIE,
                accessTokenResponseBody.getRefreshToken(),
                REFRESH_TOKEN_EXPIRATION_TIME
        ));
        response.sendRedirect(PUBLIC_URL);
    }

    @GetMapping(value = "/weight", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody WeightResponse weight(
            @CookieValue(required = false, name = ACCESS_TOKEN_COOKIE) String accessToken,
            @CookieValue(required = false, name = REFRESH_TOKEN_COOKIE) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (accessToken == null) {
            accessToken = authenticate(refreshToken, response);
        }

        WeightResponse weightResponse = new WeightResponse();
        weightResponse.setWeight(withingsService.getFirstMeasureValue(withingsService.getMeasure(accessToken)));
        return weightResponse;
    }

    private String authenticate(String refreshToken, HttpServletResponse response) {
        GetAccessTokenResponse accessTokenResponse = withingsService.refreshAccessToken(refreshToken);

        if (accessTokenResponse.getStatus() != 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        GetAccessTokenResponseBody accessTokenResponseBody = accessTokenResponse.getBody();
        response.addCookie(getSecureCookie(
                ACCESS_TOKEN_COOKIE,
                accessTokenResponseBody.getAccessToken(),
                accessTokenResponseBody.getExpiresIn()
        ));
        response.addCookie(getSecureCookie(
                REFRESH_TOKEN_COOKIE,
                accessTokenResponseBody.getRefreshToken(),
                REFRESH_TOKEN_EXPIRATION_TIME
        ));
        return accessTokenResponseBody.getAccessToken();
    }

    private Cookie getSecureCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

}
