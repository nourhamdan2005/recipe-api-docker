package projectoop.recipeApi.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 1 day (in ms)

    // ✅ Generate JWT including username and role
    public static String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // ✅ Extract username from token
    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ Extract role from token
    public static String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ✅ Validate token: check expiration and username match
    public static boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username)
                && !getClaims(token).getExpiration().before(new Date());
    }

    // ✅ Helper to parse claims from token
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace("Bearer ", "")) // in case "Bearer ..." format
                .getBody();
    }
}