package keysson.apis.validacao.controller;

import keysson.apis.validacao.dto.request.RequestRegister;
import keysson.apis.validacao.exception.BusinessRuleException;
import keysson.apis.validacao.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
public class RegisterControllerImpl implements RegisterController {

    private final RegisterService registerService;

    @Override
    public void register(@RequestBody RequestRegister requestRegister, String token) throws BusinessRuleException, SQLException {
        registerService.registerEmployee(requestRegister);
    }
}
