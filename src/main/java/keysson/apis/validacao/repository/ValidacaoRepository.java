package keysson.apis.validacao.repository;

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
            SELECT * FROM users
            WHERE username = ?
            """;

    public User findByUsername(String username) {
        return jdbcTemplate.query(FIND_BY_USERNAME, new Object[]{username}, rs -> {
            if (rs.next()) {
                return userRowMapper.mapRow(rs, 1);
            }
            return null;
        });
    }
}
