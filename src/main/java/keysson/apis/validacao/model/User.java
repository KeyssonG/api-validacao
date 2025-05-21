package keysson.apis.validacao.model;

import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private Long id;
    private Long companyId;
    private String username;
    private String password;
    private int status;
    private UUID consumerId;
}
