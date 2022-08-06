package mucsi96.trainingLog.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import mucsi96.trainingLog.config.JwtConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

@Component
public class CookieBasedAuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final Algorithm algorithm;

    public CookieBasedAuthorizedClientRepository(
            ClientRegistrationRepository clientRegistrationRepository,
            JwtConfig jwtConfig
    ) {
        this.algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public OAuth2AuthorizedClient loadAuthorizedClient(
            String clientRegistrationId,
            Authentication principal,
            HttpServletRequest request
    ) {
        Cookie cookie = WebUtils.getCookie(request, clientRegistrationId);

        if (cookie == null) {
            return null;
        }

        return getAuthorizedClient(clientRegistrationId, cookie.getValue());
    }

    @Override
    public void saveAuthorizedClient(
            OAuth2AuthorizedClient authorizedClient,
            Authentication principal,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = getAuthorizedClientToken(authorizedClient, principal);

        Cookie cookie = new Cookie(authorizedClient.getClientRegistration().getRegistrationId(), token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public void removeAuthorizedClient(
            String clientRegistrationId,
            Authentication principal,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie cookie = new Cookie(clientRegistrationId, null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getAuthorizedClientToken(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        return JWT
                .create()
                .withSubject(authorizedClient.getPrincipalName())
                .withIssuedAt(authorizedClient.getAccessToken().getIssuedAt())
                .withClaim("expiresAt", authorizedClient.getAccessToken().getExpiresAt())
                .withClaim("accessToken", authorizedClient.getAccessToken().getTokenValue())
                .withClaim(
                        "refreshToken",
                        authorizedClient.getRefreshToken() != null ?
                                authorizedClient.getRefreshToken().getTokenValue() : null
                )
                .withClaim("scopes", List.copyOf(authorizedClient.getAccessToken().getScopes()))
                .sign(algorithm);
    }

    private OAuth2AuthorizedClient getAuthorizedClient(
            String clientRegistrationId,
            String token
    ) {
        DecodedJWT jwt = JWT
                .require(algorithm)
                .build()
                .verify(token);
        return new OAuth2AuthorizedClient(
                clientRegistrationRepository.findByRegistrationId(clientRegistrationId),
                jwt.getSubject(),
                new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        jwt.getClaim("accessToken").asString(),
                        jwt.getIssuedAtAsInstant(),
                        jwt.getClaim("expiresAt").asInstant(),
                        Set.copyOf(jwt.getClaim("scopes").asList(String.class))
                ),
                new OAuth2RefreshToken(
                        jwt.getClaim("refreshToken").asString(),
                        jwt.getIssuedAtAsInstant()
                )
        );
    }
}
