package shared.dtos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ClienteDTO implements Serializable{
    private String uuid;
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
}
