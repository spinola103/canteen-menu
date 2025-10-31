package com.app.canteen.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.app.canteen.model.Role;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final SecretKey key = Keys.hmacShaKeyFor("canteenSecretKeyForJwtAuth1234567890!".getBytes());
  private final long EXPIRY = 1000 * 60 * 60 * 6; // 6 hours

  public String generateToken(String id, Role role) {
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("role", role);
	    return Jwts.builder()
	        .setClaims(claims)
	        .setSubject(id)
	        .setIssuedAt(new Date(System.currentTimeMillis()))
	        .setExpiration(new Date(System.currentTimeMillis() + EXPIRY))
	        .signWith(key)
	        .compact();
	}


  public Jws<Claims> validate(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
  }
}
