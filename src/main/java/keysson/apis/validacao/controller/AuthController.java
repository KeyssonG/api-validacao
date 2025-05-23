package keysson.apis.validacao.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import keysson.apis.validacao.dto.request.LoginRequest;
import keysson.apis.validacao.dto.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

public interface AuthController {

    @PostMapping("/login")
    @Operation(
            summary = "Login do usuário",
            description = "Endpoint que autentica usuário através do username e password, e gera Token.",
            requestBody = @RequestBody(
                    description = "Dados de login do usuário.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
    )
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request);
}
