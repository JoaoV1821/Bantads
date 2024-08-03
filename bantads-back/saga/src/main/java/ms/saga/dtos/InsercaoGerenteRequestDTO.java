package ms.saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsercaoGerenteRequestDTO {
    
    private String cpf;
    private String email;
    private String telefone;
}
