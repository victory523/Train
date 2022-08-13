package mucsi96.trainingLog.oauth;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
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
