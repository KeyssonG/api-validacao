package keysson.apis.validacao.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import keysson.apis.validacao.dto.response.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@AllArgsConstructor
public class JwtUtil {

    private final String SECRET_KEY = "0MEI6P/upC+MtYqAiki5pV0Zwpnm8zo12OLnLK6SQWI=";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public LoginResponse generateToken(Long id, Long companyId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .claim("id", id)
                .claim("companyId", companyId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        return new LoginResponse(token, expiration);
    }
}
