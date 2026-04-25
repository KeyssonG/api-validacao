package keysson.apis.validacao.repository;

import keysson.apis.validacao.dto.MensagensPendentes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RabbitRepository {

    private final JdbcTemplate jdbcTemplate;

    public RabbitRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String INSERT_MENSSAGE = """
    INSERT INTO MENSAGENS_PENDENTES (id, name, email, cnpj_cpf, username, status)
    VALUES (?, ?, ?, ?, ?, ?)
    """;

    public void saveMenssage(MensagensPendentes mensagem) {
        jdbcTemplate.update(INSERT_MENSSAGE,
                mensagem.getIdEmpresa(),
                mensagem.getName(),
                mensagem.getEmail(),
                mensagem.getCpf(),
                mensagem.getUsername(),
                mensagem.getStatus());
    }

}