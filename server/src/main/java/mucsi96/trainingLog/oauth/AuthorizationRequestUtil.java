package mucsi96.trainingLog.oauth;

import lombok.RequiredArgsConstructor;
import mucsi96.trainingLog.google.oauth.GoogleClient;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthorizationRequestUtil {
  private final MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;
  public void sendAuthorizationRequestModel(HttpServletResponse response, String registrationId) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
    String oauth2LoginUrl = ServletUriComponentsBuilder.fromCurrentServletMapping().path(
      OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/" + registrationId
    ).build().toString();

    RepresentationModel model = RepresentationModel
      .of(null)
      .add(Link.of(oauth2LoginUrl).withRel("oauth2Login"));

    try {
      jackson2HttpMessageConverter.write(
        model,
        RepresentationModel.class,
        MediaType.APPLICATION_JSON,
        servletServerHttpResponse
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
