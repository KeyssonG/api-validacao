package keysson.apis.validacao.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import keysson.apis.validacao.dto.response.LoginResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@Getter
public class JwtUtil {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public LoginResponse generateToken(Long id, Long companyId, UUID consumerId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .claim("id", id)
                .claim("companyId", companyId)
                .claim("consumerId", consumerId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        return new LoginResponse(token, expiration);
    }
}
