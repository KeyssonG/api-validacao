package keysson.apis.validacao.repository;

import keysson.apis.validacao.dto.request.RequestUpdateEmployee;
import keysson.apis.validacao.dto.response.FuncionarioRegistroResultado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RegisterRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall cadastrarFuncionarioCall;
    private final SimpleJdbcCall atualizarFuncionarioCall;

    public RegisterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.cadastrarFuncionarioCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("proc_cadastrar_funcionario");
        this.atualizarFuncionarioCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("proc_atualizar_funcionario");
    }

    private static final String CHECK_EXISTS_CPF = """
            SELECT COUNT(*)
            FROM funcionarios
            WHERE cpf = ?
            """;

    private static final String CHECK_EXISTS_USERNAME = """
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

    private static final String CHECK_EXISTS_EMAIL = """
                 SELECT COUNT(*)
                 FROM contatos
                 WHERE email = ?
            """;


    public FuncionarioRegistroResultado save(int idEmpresa, String nome, java.sql.Date dataNascimento, String departamento, String telefone, String email,
                                             String cpf, String endereco, String sexo, String username, String password, int numeroMatricula) {

        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("p_id_empresa", idEmpresa)
                .addValue("p_nome", nome)
                .addValue("p_data_nascimento", dataNascimento)
                .addValue("p_departamento", departamento)
                .addValue("p_telefone", telefone)
                .addValue("p_email", email)
                .addValue("p_cpf", cpf)
                .addValue("p_endereco", endereco)
                .addValue("p_sexo", sexo)
                .addValue("p_username", username)
                .addValue("p_password", password)
                .addValue("p_numero_matricula", numeroMatricula);

        Map<String, Object> result = cadastrarFuncionarioCall.execute(inParams);

        System.out.println("Map result: " + result);

        Integer resultCode = (Integer) result.get("out_result");
        Integer idFuncionario = (Integer) result.get("out_user_id");

        return new FuncionarioRegistroResultado(resultCode, idFuncionario);
    }


    public boolean existsByRegistration(int numeroMatricula) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_NUMERO_MATRICULA, Long.class, numeroMatricula);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_EMAIL, Long.class, email);
        return count != null && count > 0;
    }

    public Integer updateEmployee(RequestUpdateEmployee request, Integer idEmpresa) {
        MapSqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("p_user_id", request.getId().intValue())
                .addValue("p_id_empresa", idEmpresa)
                .addValue("p_nome", request.getNome())
                .addValue("p_departamento", request.getDepartamento())
                .addValue("p_telefone", request.getTelefone())
                .addValue("p_email", request.getEmail())
                .addValue("p_cpf", request.getCpf())
                .addValue("p_endereco", request.getEndereco())
                .addValue("p_sexo", request.getSexo())
                .addValue("p_data_nascimento", Optional.ofNullable(request.getDataNascimento())
                        .map(Date::getTime)
                        .map(java.sql.Date::new)
                        .orElse(null));

        Map<String, Object> result = atualizarFuncionarioCall.execute(inParams);

        return Optional.ofNullable(result.get("out_result"))
                .map(o -> (Integer) o)
                .orElse(0);
    }
}
