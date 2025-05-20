package keysson.apis.validacao.service;

import keysson.apis.validacao.Utils.JwtUtil;
import keysson.apis.validacao.dto.request.LoginRequest;
import keysson.apis.validacao.dto.response.LoginResponse;
import keysson.apis.validacao.exception.BusinessRuleException;
import keysson.apis.validacao.exception.enums.ErrorCode;
import keysson.apis.validacao.model.User;
import keysson.apis.validacao.repository.ValidacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ValidacaoRepository validacaoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login (LoginRequest request) {
            User user = validacaoRepository.findByUsername(request.getUsername());

            if (user == null) {
                throw new BusinessRuleException(ErrorCode.USER_NOT_FOUND);
            }

            Boolean checkPassword = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (checkPassword == false) {
                throw new BusinessRuleException(ErrorCode.BAD_PASSWORD);
            }

            int status = user.getStatus();

            if (status == 1) {
                validacaoRepository.activeAccount(user.getId(), user.getCompanyId(), user.getUsername());
            }

            return jwtUtil.generateToken(user.getId(), user.getCompanyId());



    }
}
