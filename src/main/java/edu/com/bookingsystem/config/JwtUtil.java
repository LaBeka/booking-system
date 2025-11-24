package edu.com.bookingsystem.config;


import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class JwtUtil {

    private final String secret = "supersecretkey123456789abcdcdcdcdcdcdcdcdcdcd";

    public String generateToken(UserAccount user) {
        List<String> rawRoles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        Set<String> roles = rawRoles.stream().map(ga -> ga.replace("ROLE_", "")).collect(Collectors.toSet());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

    }

    public Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

}
