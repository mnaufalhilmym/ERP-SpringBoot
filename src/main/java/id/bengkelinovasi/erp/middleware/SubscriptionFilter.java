package id.bengkelinovasi.erp.middleware;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import id.bengkelinovasi.erp.repository.SubscriptionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SubscriptionFilter extends OncePerRequestFilter {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        subscriptionRepository
                .findActiveByUserID(UUID.fromString(userDetails.getUsername()))
                .orElseThrow(() -> new NoSuchElementException("Layanan belum aktif"));

        filterChain.doFilter(request, response);
    }

}
