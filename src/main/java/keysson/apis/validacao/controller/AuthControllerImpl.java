package keysson.apis.validacao.controller;

import keysson.apis.validacao.dto.request.ConfirmResetPassword;
import keysson.apis.validacao.dto.request.LoginRequest;
import keysson.apis.validacao.dto.request.RequestResetPassword;
import keysson.apis.validacao.dto.response.LoginResponse;
import keysson.apis.validacao.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController{

    private final AuthService authService;

    @Override
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> requestPasswordReset(@RequestBody RequestResetPassword request) throws SQLException {
        authService.requestPasswordChange(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> confirmPasswordReset(@RequestBody ConfirmResetPassword request) throws SQLException {
        authService.validatePasswordReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
