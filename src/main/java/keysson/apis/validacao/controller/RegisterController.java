package keysson.apis.validacao.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import keysson.apis.validacao.dto.request.RequestRegister;
import keysson.apis.validacao.dto.request.RequestUpdateEmployee;
import keysson.apis.validacao.exception.BusinessRuleException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.sql.SQLException;

public interface RegisterController {

    @PostMapping("/cadastrar/funcionario-cliente")
    @Operation(
            summary = "Cadastrar um novo funcionário.",
            description = "Endpoint para cadastrar um novo funcionário.",
            requestBody = @RequestBody(
                    description = "Dados da nova empresa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RequestRegister.class)
                    )
            )
    )
    public void register(@RequestBody RequestRegister requestRegister,
                         @RequestHeader("Authorization") String token)
            throws BusinessRuleException, SQLException;


    @PutMapping("/employee/update")
    @Operation(
            summary = "Atualiza dados do funcionário.",
            description = "Atualiza dados do funcionário por Id."
    )
    Void updateEmployeeData(
            @RequestHeader("Authorization") String token,
            @RequestBody RequestUpdateEmployee requestBody
    ) throws BusinessRuleException, SQLException;
}

