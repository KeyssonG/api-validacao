package keysson.apis.validacao.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@Getter
@Slf4j
public class JwtUtil {

    private static final long EXPIRATION_TIME = MILLISECONDS.toMillis(86400000);
    private final Key key;
    private final JwtParser jwtParser;

    public JwtUtil(@Value("${SECRET_KEY}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        // Cache do parser para reutilização (reduz alocação de memória)
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    public String generateToken(int id, int companyId, UUID consumerId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .claim("id", id)
                .claim("companyId", companyId)
                .claim("consumerId", consumerId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }
    
    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    }

    public Claims extractAllClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Integer extractUserId(String token) {
        return extractAllClaims(token).get("id", Integer.class);
    }

    public Integer extractCompanyId(String token) {
        return extractAllClaims(token).get("companyId", Integer.class);
    }

    public UUID extractConsumerId(String token) {
        String consumerIdStr = extractAllClaims(token).get("consumerId", String.class);
        return UUID.fromString(consumerIdStr);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token expirado em: {}", e.getClaims().getExpiration());
        } catch (MalformedJwtException e) {
            log.debug("Token malformado: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.debug("Assinatura inválida. Chave correta?");
        } catch (Exception e) {
            log.debug("Erro inesperado: {} - {}", e.getClass(), e.getMessage());
        }
        return false;
    }
}

