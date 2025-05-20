package keysson.apis.validacao.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
        USER_NOT_FOUND("Usuário não existe.", HttpStatus.BAD_REQUEST),
        BAD_PASSWORD("Senha incorreta", HttpStatus.BAD_REQUEST),
        ERROR_ACTIVE_ACCOUNT("Erro ao tentar ativar a conta", HttpStatus.BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
