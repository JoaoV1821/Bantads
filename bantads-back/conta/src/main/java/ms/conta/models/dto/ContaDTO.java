package ms.conta.models.dto;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContaDTO {
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
