package keysson.apis.validacao.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SimpleErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
