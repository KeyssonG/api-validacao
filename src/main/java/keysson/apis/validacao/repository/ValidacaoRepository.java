package keysson.apis.validacao.repository;

import keysson.apis.validacao.exception.BusinessRuleException;
import keysson.apis.validacao.exception.enums.ErrorCode;
import keysson.apis.validacao.mapper.UserRowMapper;
import keysson.apis.validacao.mapper.PasswordResetTokenRowMapper;
import keysson.apis.validacao.model.PasswordResetToken;
import keysson.apis.validacao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class ValidacaoRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRowMapper userRowMapper;

    @Autowired
    private PasswordResetTokenRowMapper passwordResetTokenRowMapper;


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

    private static final String FIND_STATUS_COMPANY = """
            SELECT STATUS FROM COMPANIES WHERE id = ?
            """;

    // Consultas para reset de senha
    private static final String FIND_USER_BY_EMAIL = """
            SELECT\s
                u.id, u.company_id, u.username, u.password, u.status,\s
                c.consumer_id, u.primeiro_acesso
            FROM\s
                users u
                JOIN companies c ON u.company_id = c.id
                JOIN contatos ct ON u.id = ct.user_id
            WHERE\s
                ct.email = ?
            LIMIT 1
            """;

    private static final String UPDATE_PASSWORD = """
            UPDATE users SET password = ? WHERE id = ?
            """;

    private static final String SAVE_RESET_TOKEN = """
            INSERT INTO password_reset_tokens (user_id, token, expires_at, created_at, used)
            VALUES (?, ?, ?, ?, false)
            """;

    private static final String FIND_VALID_RESET_TOKEN = """
            SELECT user_id, token, expires_at, created_at, used
            FROM password_reset_tokens
            WHERE token = ? AND expires_at > ? AND used = false
            """;

    private static final String MARK_TOKEN_AS_USED = """
            UPDATE password_reset_tokens SET used = true WHERE token = ?
            """;



    public User findByUsername(String username, int idEmpresa) {
        return jdbcTemplate.query(FIND_BY_USERNAME, new Object[]{username, idEmpresa}, rs -> {
            if (rs.next()) {
                return userRowMapper.mapRow(rs, 1);
            }
            return null;
        });
    }
    public void activeAccount (int idUser, int idEmpresa, String username) {
        try {
            jdbcTemplate.update(ACCOUNT_ACTIVATION, idUser, idEmpresa, username);
        } catch (Exception e) {
            throw new BusinessRuleException(ErrorCode.ERROR_ACTIVE_ACCOUNT);
        }
    }

    public int findStatusCompany(int idEmpresa) {
        try {
            return jdbcTemplate.queryForObject(FIND_STATUS_COMPANY, new Object[]{idEmpresa}, Integer.class);
        } catch (Exception e) {
            throw new BusinessRuleException(ErrorCode.ERRO_STATUS_COMPANY);
        }
    }


    public void saveNewPassword(String newPassword, Integer userId) throws SQLException {
        try {
            jdbcTemplate.update(UPDATE_PASSWORD, newPassword, userId);
        } catch (Exception ex) {
            throw new SQLException("Erro ao tentar atualizar a senha" + ex.getMessage(), ex);
        }
    }

    // Métodos para reset de senha
    public User findByUsernameAndEmail(String email) {
        return jdbcTemplate.query(FIND_USER_BY_EMAIL, new Object[]{email}, rs -> {
            if (rs.next()) {
                return userRowMapper.mapRow(rs, 1);
            }
            return null;
        });
    }

    public void saveResetToken(Long userId, String token, LocalDateTime expiresAt) throws SQLException {
        try {
            jdbcTemplate.update(SAVE_RESET_TOKEN, userId, token, expiresAt, LocalDateTime.now());
        } catch (Exception ex) {
            throw new SQLException("Erro ao salvar token de reset de senha: " + ex.getMessage(), ex);
        }
    }

    public PasswordResetToken findValidResetToken(String token) {
        return jdbcTemplate.query(FIND_VALID_RESET_TOKEN,
                new Object[]{token, LocalDateTime.now()}, rs -> {
                    if (rs.next()) {
                        return passwordResetTokenRowMapper.mapRow(rs, 1);
                    }
                    return null;
                });
    }

    public void markTokenAsUsed(String token) throws SQLException {
        try {
            jdbcTemplate.update(MARK_TOKEN_AS_USED, token);
        } catch (Exception ex) {
            throw new SQLException("Erro ao marcar token como usado: " + ex.getMessage(), ex);
        }
    }
}
