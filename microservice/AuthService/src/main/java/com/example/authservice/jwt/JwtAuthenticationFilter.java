package com.example.authservice.jwt;

import com.example.authservice.response.Response;
import com.example.authservice.security.AccountDetail;
import com.example.authservice.security.AccountDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AccountDetailService accountDetailService;

    private static final String[] PATH_WHITELIST = {
            "/api/v1/auth"
    };


    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra header Authorization có jwt token ko
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Kiểm tra nếu đường dẫn trong danh sách bỏ qua
        if (isWhitelisted(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String jwt = getJwtFromRequest(request);
            if(StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt, false)){
                String username = jwtProvider.getSubjectFromJwt(jwt, false);
                AccountDetail accountDetail = (AccountDetail) accountDetailService.loadUserByUsername(username);
                if(accountDetail != null){
                    // Nếu hợp lệ, set thông tin cho security context
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(accountDetail, null, accountDetail.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch(ExpiredJwtException ex){
            Response errorResponse = new Response(false, "TOKEN EXPIRED", null);
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        } catch(Exception ex){
            Response errorResponse = new Response(false, ex.getMessage(), null);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        }
    }

    private boolean isWhitelisted(String requestURI) {
        for (String path : PATH_WHITELIST) {
            if (requestURI.startsWith(path)) {
                return true;
            }
        }
        return false;
    }
}
