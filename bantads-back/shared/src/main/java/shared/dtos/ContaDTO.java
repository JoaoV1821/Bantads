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
    private String id_cliente;
    //GERENTE
    private String id_gerente;

    private int estado;


}
