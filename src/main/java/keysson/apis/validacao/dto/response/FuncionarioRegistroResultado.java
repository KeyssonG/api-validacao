package keysson.apis.validacao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FuncionarioRegistroResultado {
    private int resultCode;
    private int idFuncionario;
}
