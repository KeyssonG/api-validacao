package keysson.apis.validacao.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class RequestUpdateEmployee {
    private Long id;
    private String nome;
    private String departamento;
    private String telefone;
    private String email;
    private String cpf;
    private String endereco;
    private String sexo;
    private Date dataNascimento;
}
