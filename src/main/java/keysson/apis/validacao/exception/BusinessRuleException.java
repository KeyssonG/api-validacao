package keysson.apis.validacao.exception;

import keysson.apis.validacao.exception.enums.ErrorCode;

public class BusinessRuleException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessRuleException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}