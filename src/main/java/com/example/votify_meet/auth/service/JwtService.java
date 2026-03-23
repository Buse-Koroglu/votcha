package com.example.votify_meet.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
 * Creates and checks token
 * */
@Service
public class JwtService {
    // Generated from generate-random.org
    // The secret key we use to sign the token.
    private static final String SECRET_KEY = "397183c659994b31bb7c9e0805441d4da92dfe92c7c663e89634a745fb6e290b";

    // Extract the username from the token
    public String extractUsername(String token) {return extractClaimByToken(token, Claims::getSubject);}

    // A general function for reading any data (claim) within a token.
    public <T> T extractClaimByToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    // This is where the token was originally created:
    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims) // We can set a role like role:admin if we want
                .setSubject(userDetails.getUsername()) // Who owns the token (Email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // When did create?
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *  60 * 15)) // Expire after 15 minutes (set this from app.properties)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with HS256 Algorithm
                .compact(); // to string
    }
    // Checks the received token is valid or not
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    // Time Control
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaimByToken(token, Claims::getExpiration);
    }


    // Parse method fot the token.
    // Throws an error here if the key is incorrect or the token is corrupted!
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
    // Converts the string SECRET_KEY into a "Key" object that Java understands.
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
