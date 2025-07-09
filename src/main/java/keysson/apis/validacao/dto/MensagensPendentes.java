package keysson.apis.validacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensagensPendentes {
    private int idEmpresa;
    private String name;
    private String email;
    private String cpf;
    private String username;
    private int status;
}