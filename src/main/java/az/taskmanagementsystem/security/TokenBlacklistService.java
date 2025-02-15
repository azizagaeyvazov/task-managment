package az.taskmanagementsystem.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final JwtService jwtService;

    public void blacklistToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header is incorrect");
        }

        final String jwt = authHeader.substring(7);
        long expirationInSeconds = Duration.between(Instant.now(), extractExpiration(jwt).toInstant()).getSeconds();

        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jwt, "blacklisted", Duration.ofSeconds(expirationInSeconds));
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    private Date extractExpiration(String token) {
        return jwtService.extractClaim(token, Claims::getExpiration);
    }
}

