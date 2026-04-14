package com.sns.analyzer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // ⭐ 수정
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // #장소영~ 권한(authorities) 포함 인증을 위해 UserDetails 로딩
    private final CustomUserDetailsService customUserDetailsService;
    // #여기까지

    @Override
protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    
    // 로그를 찍어서 실제 path가 어떻게 찍히는지 서버에서 확인해야 합니다.
    System.out.println("DEBUG: request path = [" + path + "]");

    // 루트(/)를 포함하여 조금 더 확실하게 예외 처리
    if (path == null || path.equals("/") || path.isEmpty() || path.equals("/index.html") || path.equals("/error")) {
        return true;
    }

    if (path.startsWith("/api/auth/")) return true;
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

    return false;
}
    // ===========================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        System.out.println("DEBUG: JwtAuthenticationFilter for " + request.getRequestURI());

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                System.out.println("DEBUG: Valid JWT for user: " + username);

                // ⭐ 수정: username(String) → UserDetails 로 변환
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // ⭐ 수정: principal에 userDetails, authorities 포함
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("DEBUG: JWT invalid or missing");
            }
        } catch (Throwable ex) {
            System.err.println("ERROR: JwtAuthenticationFilter failed: " + ex.getMessage());
            ex.printStackTrace();
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
