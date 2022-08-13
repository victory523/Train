package mucsi96.trainingLog.oauth;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.google.oauth.GoogleClient;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final AuthorizationRequestUtil authorizationRequestUtil;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    authorizationRequestUtil.sendAuthorizationRequestModel(response, GoogleClient.id);
  }
}
