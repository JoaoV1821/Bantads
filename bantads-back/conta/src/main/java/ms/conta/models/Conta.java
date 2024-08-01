package ms.conta.models;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Conta {
    @Id
    private String id;
    private Double limite;
    private Double saldo;
    private Date data; //Criação ou Rejeição
    //CLIENTE
    private String id_cliente;
    //GERENTE
    private String id_gerente;

    private int estado;

}
