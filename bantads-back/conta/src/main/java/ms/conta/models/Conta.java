package ms.conta.models;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double limite;
    private Double saldo;
    private DateTimeFormat data; //Criação ou Rejeição
    //CLIENTE
    private Long id_cliente;
    private String cpf_cliente;
    private String nome_cliente;
    //GERENTE
    private Long id_gerente;
    private String cpf_gerente;
    private String nome_gerente;

}
