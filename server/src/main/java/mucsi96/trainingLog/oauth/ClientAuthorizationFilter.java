package mucsi96.trainingLog.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ClientAuthorizationFilter extends GenericFilterBean {
  private final AuthorizationRequestUtil authorizationRequestUtil;
  private final ThrowableAnalyzer throwableAnalyzer = new ThrowableAnalyzer();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
    try {
      chain.doFilter(request, response);
    } catch (Exception exception) {
      Throwable[] causeChain = throwableAnalyzer.determineCauseChain(exception);
      ClientAuthorizationRequiredException authorizationRequiredException = (ClientAuthorizationRequiredException) this.throwableAnalyzer
        .getFirstThrowableOfType(ClientAuthorizationRequiredException.class, causeChain);

      if (authorizationRequiredException != null) {
        authorizationRequestUtil.sendAuthorizationRequestModel(
          (HttpServletResponse) response,
          authorizationRequiredException.getClientRegistrationId()
        );
      } else {
        throw exception;
      }
    }
  }
}
