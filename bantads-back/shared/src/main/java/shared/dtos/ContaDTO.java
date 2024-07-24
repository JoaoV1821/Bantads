package shared.dtos;

import java.io.Serializable;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ContaDTO implements Serializable{

    private Long id;
    private Double limite;
    private Double saldo;
    private Date data; //Criação ou Rejeição
    //CLIENTE
    private Long id_cliente;
    private String cpf_cliente;
    private String nome_cliente;
    //GERENTE
    private Long id_gerente;
    private String cpf_gerente;
    private String nome_gerente;


}
