package keysson.apis.validacao.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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

    private static final long EXPIRATION_TIME = MILLISECONDS.toMillis(86400000);
    private final Key key;

    public JwtUtil(@Value("${SECRET_KEY}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
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
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token expirado em: " + e.getClaims().getExpiration());
        } catch (MalformedJwtException e) {
            System.err.println("Token malformado: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Assinatura inválida. Chave correta?");
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getClass() + " - " + e.getMessage());
        }
        return false;
    }
}

