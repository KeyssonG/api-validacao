package keysson.apis.validacao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Date expiresAt;
}
