package com.example.socinet.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtProvider {
    @Value("${jwt.secret}")
    private String ACCESS_KEY;
    @Value("${jwt.refresh.secret}")
    private String REFRESH_KEY;
    private final long accessExpired = 60 * 60 * 1000;
    private final long refreshExpired = 30L * 24 * 60 * 60 * 1000;

    public String generateAccessToken(String username){
        Date now = new Date(System.currentTimeMillis());
        Date expiredAt = new Date(now.getTime() + accessExpired);
        return Jwts.builder().setSubject(username)
                .signWith(SignatureAlgorithm.HS256, ACCESS_KEY)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .compact();
    }

    public String generateRefreshToken(String username) throws Exception {
        Date now = new Date(System.currentTimeMillis());
        Date expiredAt = new Date(now.getTime() + refreshExpired);
        return Jwts.builder().setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS256, REFRESH_KEY)
                .compact();
    }

    public String getSubjectFromJwt(String token, boolean isRefreshToken){
        String signingKey = isRefreshToken ? REFRESH_KEY : ACCESS_KEY;
        Claims claims = Jwts.parser().setSigningKey(signingKey)
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token, boolean isRefreshToken) throws ExpiredJwtException{
        String signingKey = isRefreshToken ? REFRESH_KEY : ACCESS_KEY;
        try{
            Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token);
            return true;
        } catch(MalformedJwtException e){
            log.error("Token is invalid");
        } catch(ExpiredJwtException e){
            log.error("Token is expired");
            throw e;
        } catch(Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
}
