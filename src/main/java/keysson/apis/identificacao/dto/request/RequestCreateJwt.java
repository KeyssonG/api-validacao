package keysson.apis.identificacao.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateJwt {

    @Schema(description = "ID da empresa", required = true)
    private String idEmpresa;
    @Schema(description = "consumerId da empresa", required = true)
    private String consumerId;
}
