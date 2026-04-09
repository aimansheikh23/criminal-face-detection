package com.criminaldetection.criminal_face_detection.security;

import com.criminaldetection.criminal_face_detection.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    public String generateToken(String username, Role role) {
        return Jwts.builder().setSubject(username)
                .claim("role", role.name()).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public String getUsernameFromToken(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Role getRoleFromToken(String token){
        String role = extractAllClaims(token).get("role", String.class);
        return Role.valueOf(role);
    }

    public Date getExpirationTimeFromToken(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDetails userDetails, HttpServletResponse response) throws IOException {
        try {
            String username = getUsernameFromToken(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            response.sendError(401, "SignatureException");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            response.sendError(401, "MalformedJwtException");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            response.sendError(401, "ExpiredJwtException");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            response.sendError(401, "UnsupportedJwtException");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            response.sendError(401, "IllegalArgumentException");
        }
        return false;
    }

    private  <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        return claimResolver.apply(extractAllClaims(token));
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token){
        return getExpirationTimeFromToken(token).before(new Date());
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
