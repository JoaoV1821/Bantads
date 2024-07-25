package ms.saga.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorRequestDTO {
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
    
    private int estado;
}
