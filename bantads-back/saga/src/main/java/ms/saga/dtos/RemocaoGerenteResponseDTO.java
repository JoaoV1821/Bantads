package ms.saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ms.saga.dtos.enums.SagaStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemocaoGerenteResponseDTO {
    
    private String id_gerente;
    private SagaStatus status;

}
