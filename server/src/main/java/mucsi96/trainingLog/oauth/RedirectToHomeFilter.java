package mucsi96.trainingLog.oauth;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Workaround until https://github.com/spring-projects/spring-security/issues/11678 is resolved
 */
@Component
public class RedirectToHomeFilter extends GenericFilterBean {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    try {
      chain.doFilter(request, response);
    } catch (RedirectToHomeException ex) {
      httpServletResponse.sendRedirect("/");
    }
  }
}
