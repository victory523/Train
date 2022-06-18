package mucsi96.trainingLog.withings;

import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.withings.data.GetAccessTokenResponse;
import mucsi96.trainingLog.withings.data.GetAccessTokenResponseBody;
import mucsi96.trainingLog.withings.WithingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;

@Slf4j
@Controller
@RequestMapping("/withings")
public class WithingsController {

    static final String ACCESS_TOKEN_COOKIE = "withings_access_token";
    static final String REFRESH_TOKEN_COOKIE = "withings_refresh_token";
    static final int REFRESH_TOKEN_EXPIRATION_TIME = 30 * 24 * 60 * 60; // 30 days

    @Autowired
    WithingsService withingsService;

    @GetMapping("/auth")
    String withings(RedirectAttributes redirectAttributes, HttpSession session) {
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        session.setAttribute("state", state);
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + withingsService.getAuthorizationCodeUrl(state);
    }

    @GetMapping("/redirect")
    String withingsAuth(
            @RequestParam("code") String authorizationCode,
            @RequestParam("state") String stateInParam,
            @SessionAttribute("state") String stateInSession,
            HttpServletResponse response
    ) {
        if (!stateInParam.equals(stateInSession)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        GetAccessTokenResponseBody accessTokenResponseBody = withingsService.getAccessToken(authorizationCode);
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
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/withings_measure";
    }

    @GetMapping("/measure")
    String withingsMeasure(
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
        
        Double value = withingsService.getFirstMeasureValue(withingsService.getMeasure(accessToken));
        log.info("Measure value {}", value);

        return "home";
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
