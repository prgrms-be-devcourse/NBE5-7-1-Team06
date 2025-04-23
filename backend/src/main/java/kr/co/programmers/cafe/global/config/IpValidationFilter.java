package kr.co.programmers.cafe.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class IpValidationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        boolean isLocal = "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);

        // 로그인 요청인 경우 IP 검증
        if ("/admin/login".equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod())) {
            if (!isLocal) {
                response.sendRedirect("/admin/login?error=ip_blocked");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
