package mucsi96.trainingLog;

import mucsi96.trainingLog.google.oauth.GoogleClient;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Map;

public class WithGoogleSecurityContextFactory implements WithSecurityContextFactory<WithMockGoogleUser> {
  @Override
  public SecurityContext createSecurityContext(WithMockGoogleUser user) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(new OAuth2AuthenticationToken(
      new DefaultOAuth2User(
        null,
        Map.of(IdTokenClaimNames.SUB, user.username()),
        IdTokenClaimNames.SUB
      ),
      null,
      GoogleClient.id)
    );
    return context;
  }
}
