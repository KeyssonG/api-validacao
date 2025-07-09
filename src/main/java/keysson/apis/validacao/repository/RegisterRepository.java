package keysson.apis.validacao.repository;

import jdk.jfr.Registered;
import keysson.apis.validacao.dto.response.FuncionarioRegistroResultado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Repository
public class RegisterRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    public RegisterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String CHECK_EXISTS_CPF= """
        SELECT COUNT(*) 
        FROM funcionarios 
        WHERE cpf = ?
        """;

    private static final String CHECK_EXISTS_USERNAME= """
        SELECT COUNT(*)
        FROM USERS
        WHERE username  = ? and company_id = 0
        """;

    private static final String CHECK_EXISTS_NUMERO_MATRICULA = """
             SELECT COUNT(*)
             FROM users
             WHERE conta_matricula = ?
        """;

    public boolean existsByCpf(String cpf) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_CPF, Long.class, cpf);
        return count != null && count > 0;
    }

    public boolean existsByUsername(String name) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_USERNAME, Long.class, name);
        return count != null && count > 0;
    }

    public FuncionarioRegistroResultado save(int idEmpresa, String nome, java.sql.Date dataNascimento, String departamento, String telefone, String email,
                                             String cpf, String endereco, String sexo, String username, String password, int numeroMatricula) {

        String sql = "CALL proc_cadastrar_funcionario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Map<String, Object> result = jdbcTemplate.call(connection -> {
            CallableStatement cs = connection.prepareCall(sql);

            cs.setInt(1, idEmpresa);
            cs.setString(2, nome);
            cs.setDate(3, dataNascimento);
            cs.setString(4, departamento);
            cs.setString(5, telefone);
            cs.setString(6, email);
            cs.setString(7, cpf);
            cs.setString(8, endereco);
            cs.setString(9, sexo);
            cs.setString(10, username);
            cs.setString(11, password);
            cs.setInt(12, numeroMatricula);
            cs.registerOutParameter(13, java.sql.Types.INTEGER); // out_result
            cs.registerOutParameter(14, java.sql.Types.INTEGER); // out_user_id
            return cs;
        }, Arrays.asList(
                new SqlParameter("p_id_empresa", Types.INTEGER),
                new SqlParameter("p_nome", Types.VARCHAR),
                new SqlParameter("p_data_nascimento", Types.DATE),
                new SqlParameter("p_departamento", Types.VARCHAR),
                new SqlParameter("p_telefone", Types.VARCHAR),
                new SqlParameter("p_email", Types.VARCHAR),
                new SqlParameter("p_cpf", Types.VARCHAR),
                new SqlParameter("p_endereco", Types.VARCHAR),
                new SqlParameter("p_sexo", Types.VARCHAR),
                new SqlParameter("p_username", Types.VARCHAR),
                new SqlParameter("p_password", Types.VARCHAR),
                new SqlParameter("p_numero_matricula", Types.INTEGER),
                new SqlOutParameter("out_result", Types.INTEGER),
                new SqlOutParameter("out_user_id", Types.INTEGER)
        ));

        System.out.println("Map result: " + result);

        Integer resultCode = (Integer) result.get("out_result");
        Integer idFuncionario = (Integer) result.get("out_user_id");

        return new FuncionarioRegistroResultado(resultCode, idFuncionario);
    }


    public boolean existsByRegistration(int numeroMatricula) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_NUMERO_MATRICULA, Long.class, numeroMatricula);
        return count != null && count > 0;
    }
}
