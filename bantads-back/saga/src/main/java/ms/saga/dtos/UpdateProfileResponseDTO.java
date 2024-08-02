package ms.saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ms.saga.dtos.enums.SagaStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileResponseDTO {

    private String id;
    private String cpf;
    private String email;
    private String nome;
    private String telefone;
    private double salario;
    //Endereço
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String uf;
    
    private int estado;
    
    private SagaStatus status;
}
