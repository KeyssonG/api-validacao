package keysson.apis.validacao.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND("Usuário não existe.", HttpStatus.BAD_REQUEST),
    BAD_PASSWORD("Senha incorreta", HttpStatus.BAD_REQUEST),
    ERROR_ACTIVE_ACCOUNT("Erro ao tentar ativar a conta", HttpStatus.BAD_REQUEST),
    ERRO_STATUS_COMPANY("Erro ao consultar status da empresa", HttpStatus.BAD_REQUEST),
    CONTA_PENDENTE("A conta empresarial está pendente", HttpStatus.BAD_REQUEST),
    CPF_JA_CADASTRADO("O CPF Ja está cadastrado", HttpStatus.BAD_REQUEST),
    ERRO_CADASTRAR("Erro ao cadastrar empresa", HttpStatus.BAD_REQUEST),
    USERNAME_JA_EXISTE("Esse Username já está em uso", HttpStatus.BAD_REQUEST),
    TOKEN_INVALIDO("Token inválido", HttpStatus.BAD_REQUEST),
    EMAIL_JA_CADASTRADO("Esse Email já está em uso", HttpStatus.BAD_REQUEST),
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
