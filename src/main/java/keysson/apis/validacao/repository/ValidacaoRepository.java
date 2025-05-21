package keysson.apis.validacao.repository;

import keysson.apis.validacao.exception.BusinessRuleException;
import keysson.apis.validacao.exception.enums.ErrorCode;
import keysson.apis.validacao.mapper.UserRowMapper;
import keysson.apis.validacao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ValidacaoRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRowMapper userRowMapper;


    private static final String FIND_BY_USERNAME = """
            SELECT
              u.id,
              u.company_id,
              u.username,
              u.password,
              u.status,
              c.consumer_id 
            FROM users u
            JOIN companies c ON u.company_id = c.id
            WHERE u.username = ? AND c.id = ?;
            """;

    private static final String ACCOUNT_ACTIVATION = """
            UPDATE USERS SET STATUS = 2 WHERE ID = ? AND COMPANY_ID = ? AND USERNAME = ?
            """;

    public User findByUsername(String username, int idEmpresa) {
        return jdbcTemplate.query(FIND_BY_USERNAME, new Object[]{username, idEmpresa}, rs -> {
            if (rs.next()) {
                return userRowMapper.mapRow(rs, 1);
            }
            return null;
        });
    }
    public void activeAccount (Long idUser, Long idEmpresa, String username) {
        try {
            jdbcTemplate.update(ACCOUNT_ACTIVATION, idUser, idEmpresa, username);
        } catch (Exception e) {
            throw new BusinessRuleException(ErrorCode.ERROR_ACTIVE_ACCOUNT);
        }
    }
}
