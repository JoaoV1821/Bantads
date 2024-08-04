package ms.saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ms.saga.dtos.enums.SagaStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsercaoGerenteResponseDTO {
    
    private String cpf;
    private String email;
    private String telefone;
    private SagaStatus status;
}
