package keysson.apis.validacao.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import keysson.apis.validacao.Utils.JwtUtil;
import keysson.apis.validacao.dto.PasswordResetEvent;
import keysson.apis.validacao.dto.request.LoginRequest;
import keysson.apis.validacao.dto.response.LoginResponse;
import keysson.apis.validacao.exception.BusinessRuleException;
import keysson.apis.validacao.exception.enums.ErrorCode;
import keysson.apis.validacao.model.PasswordResetToken;
import keysson.apis.validacao.model.User;
import keysson.apis.validacao.repository.ValidacaoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ValidacaoRepository validacaoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login (LoginRequest request) {
            User user = validacaoRepository.findByUsername(request.getUsername(), request.getIdEmpresa());
            int statuCompany = validacaoRepository.findStatusCompany(request.getIdEmpresa());
            if (user == null) {
                throw new BusinessRuleException(ErrorCode.USER_NOT_FOUND);
            }

            Boolean checkPassword = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (checkPassword == false) {
                throw new BusinessRuleException(ErrorCode.BAD_PASSWORD);
            }

            int status = user.getStatus();

            if (statuCompany == 1) {
                throw new BusinessRuleException(ErrorCode.CONTA_PENDENTE);
            }

            if (status == 1) {
                validacaoRepository.activeAccount(user.getId(), user.getCompanyId(), user.getUsername());
            }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getCompanyId(),
                user.getConsumerId());

        return new LoginResponse(token, jwtUtil.getExpirationDate());

    }

    @Transactional
    public void validatePasswordReset(String token, String newPassword) throws SQLException {
        // Busca o token válido
        PasswordResetToken resetToken = validacaoRepository.findValidResetToken(token);
        if (resetToken == null) {
            throw new BusinessRuleException(ErrorCode.TOKEN_INVALIDO);
        }

        // Criptografa a nova senha
        String newEncryptedPassword = passwordEncoder.encode(newPassword);

        // Atualiza a senha do usuário
        validacaoRepository.saveNewPassword(newEncryptedPassword, resetToken.getUserId().intValue());

        // Marca o token como usado
        validacaoRepository.markTokenAsUsed(token);
    }

    @Transactional
    public void requestPasswordChange(String email) throws SQLException {

        // Busca o usuário pelo username e email na tabela contatos
        User user = validacaoRepository.findByUsernameAndEmail(email);
        if (user == null) {
            throw new BusinessRuleException(ErrorCode.USER_NOT_FOUND);
        }

        // Gera um token único
        int tokenInt = 100000 + (int) (Math.random() * 900000);
        String token = String.valueOf(tokenInt);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(3);

        // Salva o token no banco
        validacaoRepository.saveResetToken((long) user.getId(), token, expiresAt);

        // Cria o evento para enviar para a fila
        PasswordResetEvent event = new PasswordResetEvent(email, token, user.getUsername());

        rabbitTemplate.convertAndSend("password.reset.queue", event);
    }
}
