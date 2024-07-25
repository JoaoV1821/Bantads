package ms.saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ms.saga.dtos.enums.SagaStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorResponseDTO {
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private Double salario;
    //Endereço
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String uf;
    
    private SagaStatus status;
}

