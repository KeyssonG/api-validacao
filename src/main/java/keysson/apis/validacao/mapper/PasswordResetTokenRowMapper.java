package keysson.apis.validacao.mapper;

import keysson.apis.validacao.model.PasswordResetToken;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PasswordResetTokenRowMapper implements RowMapper<PasswordResetToken> {

    @Override
    public PasswordResetToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(rs.getLong("user_id"));
        token.setToken(rs.getString("token"));
        token.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        token.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        token.setUsed(rs.getBoolean("used"));
        return token;
    }
}