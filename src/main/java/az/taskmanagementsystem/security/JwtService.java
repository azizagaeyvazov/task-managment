package az.taskmanagementsystem.security;

import az.taskmanagementsystem.dto.LogoutRequest;
import az.taskmanagementsystem.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.security.secretKey}")
    private String secretKey;

    @Value("${app.security.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${app.security.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Value("${app.security.token.blacklist-prefix}")
    private String BLACKLIST_PREFIX;

    private final StringRedisTemplate redisTemplate;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("tokenType", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(
            UserDetails userDetails
    ) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("tokenType", "ACCESS");
        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("tokenType", "REFRESH");
        return buildToken(extraClaims, userDetails, refreshTokenExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration((new Date(System.currentTimeMillis() + expiration)))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        return (!isTokenExpired(token) && !isTokenBlacklisted(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void blacklistToken(LogoutRequest request) {
        var accessToken = request.getAccessToken();
        var refreshToken = request.getRefreshToken();

        if (!isTokenValid(accessToken) || !isTokenValid(refreshToken)) {
            throw new InvalidTokenException();
        }
        long accessTokenExpiration = Duration.between(Instant.now(), extractExpiration(accessToken).toInstant()).getSeconds();
        long refreshTokenExpiration = Duration.between(Instant.now(), extractExpiration(refreshToken).toInstant()).getSeconds();

        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, "blacklisted", Duration.ofSeconds(accessTokenExpiration));
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + refreshToken, "blacklisted", Duration.ofSeconds(refreshTokenExpiration));
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
