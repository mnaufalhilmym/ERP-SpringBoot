package id.bengkelinovasi.erp.middleware;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import id.bengkelinovasi.erp.entity.UserSession;
import id.bengkelinovasi.erp.service.UserSessionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/") ||
                requestURI.startsWith("/api/actuator") ||
                requestURI.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userAgent = request.getHeader("User-Agent");
        if (userAgent.isEmpty()) {
            throw new ServletException("Permintaan tidak diizinkan");
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank() || authHeader.length() <= 7) {
            throw new ServletException("Sesi masuk tidak ditemukan");
        }
        final String token = authHeader.substring(7);

        UserSession userSession = userSessionService.getByTokenAndUserAgent(token, userAgent)
                .orElseThrow(() -> new EntityNotFoundException("Sesi masuk tidak ditemukan"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userSession.getUser().getId().toString());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        userSessionService.extendExpirationTime(userSession);

        filterChain.doFilter(request, response);
    }

}
