package keysson.apis.validacao.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRegister {

    @NotBlank(message = "Nome do funcionário obrigatório.")
    private String nome;

    @NotBlank(message = "Data de nascimento do funcionário obrigatório.")
    @Size(max = 10, message = "A data de nascimento deve ter no máximo 10 caracteres")
    private Date dataNascimento;

    @NotBlank(message = "Telefone do funcionário obrigatório.")
    @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres")
    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{10,15}$", message = "O telefone deve conter apenas números e ter entre 10 a 15 dígitos")
    private String telefone;

    @NotBlank(message = "E-mail Corporativo obrigatório.")
    private String email;

    @NotBlank(message = "O CPF é obrigatório, somente números")
    @Size(max = 11, message = "O CPF deve ter no máximo 11 caracteres")
    @Pattern(regexp = "^[0-9]{11}$", message = "O CPF deve conter apenas números e ter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Endereço do funcionário obrigatório.")
    @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
    private String endereco;

    @Size(max = 1, message = "Preencha o campo com M para masculino ou F para feminino ou I indefinido")
    private String sexo;

    private String username;

    @NotBlank(message = "Departamento que o funcionário pertence")
    private String departamento;
}

