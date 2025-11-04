package keysson.apis.validacao.controller;

import keysson.apis.validacao.dto.request.RequestRegister;
import keysson.apis.validacao.dto.request.RequestUpdateEmployee;
import keysson.apis.validacao.exception.BusinessRuleException;
import keysson.apis.validacao.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegisterControllerImpl implements RegisterController {

    private final RegisterService registerService;

    @Override
    public void register(@RequestBody RequestRegister requestRegister, String token) throws BusinessRuleException, SQLException {
        registerService.registerEmployee(requestRegister);
    }

    @Override
    public Void updateEmployeeData(String token, @RequestBody RequestUpdateEmployee requestBody) throws BusinessRuleException, SQLException {
        log.info("Recebida requisição para atualizar funcionário id={}", requestBody.getId());
        registerService.updateEmployeeData(requestBody);
        return null;
    }
}
