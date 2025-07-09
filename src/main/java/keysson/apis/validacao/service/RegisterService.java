package keysson.apis.validacao.service;

import jakarta.servlet.http.HttpServletRequest;
import keysson.apis.validacao.Utils.JwtUtil;
import keysson.apis.validacao.dto.FuncionarioCadastradoEvent;
import keysson.apis.validacao.dto.request.RequestRegister;
import keysson.apis.validacao.dto.response.FuncionarioRegistroResultado;
import keysson.apis.validacao.exception.BusinessRuleException;
import keysson.apis.validacao.exception.enums.ErrorCode;
import keysson.apis.validacao.repository.RegisterRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

@Service
public class RegisterService {

    private final RegisterRepository registerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RabbitService rabbitService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    private JwtUtil jwtUtil;

    public RegisterService(RegisterRepository registerRepository, BCryptPasswordEncoder passwordEncoder, RabbitService rabbitService) {
        this.registerRepository = registerRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitService = rabbitService;
    }

    public void registerEmployee (RequestRegister requestRegister) throws BusinessRuleException, SQLException {

        String token = (String) httpRequest.getAttribute("CleanJwt");

        Integer idEmpresa = jwtUtil.extractCompanyId(token);
        if (idEmpresa == null) {
            throw new IllegalArgumentException("ID da empresa não encontrado no token.");
        }

        if (registerRepository.existsByCpf(requestRegister.getCpf())) {
            throw new BusinessRuleException(ErrorCode.CPF_JA_CADASTRADO);
        }

        if (registerRepository.existsByUsername(requestRegister.getUsername())) {
            throw new BusinessRuleException(ErrorCode.USERNAME_JA_EXISTE);
        }

        int numeroMatricula = gerarNumeroMatricula();

        String plainPassword = generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);

        FuncionarioRegistroResultado resultado = registerRepository.save(
                idEmpresa,
                requestRegister.getNome(),
                requestRegister.getDataNascimento(),
                requestRegister.getTelefone(),
                requestRegister.getEmail(),
                requestRegister.getCpf(),
                requestRegister.getEndereco(),
                requestRegister.getSexo(),
                encodedPassword,
                requestRegister.getUsername(),
                requestRegister.getDepartamento(),
                numeroMatricula
        );

        if (resultado.getResultCode() == 0) {
            FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(
                    idEmpresa,
                    requestRegister.getNome(),
                    requestRegister.getEmail(),
                    requestRegister.getCpf(),
                    requestRegister.getUsername(),
                    plainPassword
            );
            try {
                rabbitTemplate.convertAndSend("funcionario-cliente.fila", event);

                rabbitService.saveMessagesInBank(event, 1);
            } catch (Exception ex) {
                rabbitService.saveMessagesInBank(event, 0);
                throw new RuntimeException("Erro ao enviar mensagem ao RabbitMQ: " + ex.getMessage());
            }
        } else if (resultado.getResultCode() == 1) {
            throw new BusinessRuleException(ErrorCode.ERRO_CADASTRAR);
        }
    }
    private int gerarNumeroMatricula() {
        Random random = new Random();
        int numero;

        do {
            numero = 100000 + random.nextInt(900000);
        } while (registerRepository.existsByRegistration(numero));

        return numero;
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 12);
    }
}
