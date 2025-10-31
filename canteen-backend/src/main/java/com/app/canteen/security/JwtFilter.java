package com.app.canteen.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtFilter implements Filter {
  private final JwtService jwtService;
  public JwtFilter(JwtService jwtService) { this.jwtService = jwtService; }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        Claims claims = jwtService.validate(token).getPayload();
        request.setAttribute("role", claims.get("role"));
        request.setAttribute("collegeId", claims.getSubject());
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
