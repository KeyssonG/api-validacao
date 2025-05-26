package keysson.apis.validacao.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import keysson.apis.validacao.dto.response.LoginResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@Getter
public class JwtUtil {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    private static final long EXPIRATION_TIME = MILLISECONDS.toMillis(86400000);
    private final Key key;

    public JwtUtil(@Value("${SECRET_KEY}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public LoginResponse generateToken(Long id, Long companyId, UUID consumerId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .claim("id", id)
                .claim("companyId", companyId)
                .claim("consumerId", consumerId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        return new LoginResponse(token, expiration);
    }
}
