package mucsi96.traininglog.core;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

public class RedirectToHomeRequestCache implements RequestCache {

  @Override
  public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
    String redirectUrl = ServletUriComponentsBuilder
        .fromCurrentServletMapping()
        .build()
        .toUri()
        .resolve("/")
        .toString();
    return new RedirectToHomeSavedRequest(redirectUrl);
  }

  @Override
  public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
  }

  @Override
  public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  @Override
  public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
  }

}

@RequiredArgsConstructor
class RedirectToHomeSavedRequest implements SavedRequest {
  private final String redirectUrl;

  @Override
  public String getRedirectUrl() {
    return redirectUrl;
  }

  @Override
  public List<Cookie> getCookies() {
    return null;
  }

  @Override
  public String getMethod() {
    return null;
  }

  @Override
  public List<String> getHeaderValues(String name) {
    return null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    return null;
  }

  @Override
  public List<Locale> getLocales() {
    return null;
  }

  @Override
  public String[] getParameterValues(String name) {
    return null;
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return null;
  }
}
