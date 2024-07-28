package ms.conta.models;

import java.sql.Date;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import ms.conta.models.enums.MovimentacaoEnum;

@Entity
@Data
@AllArgsConstructor
public class Movimentacao {
    
    @Id
    private String id;
    private Date data;
    private MovimentacaoEnum tipo;
    private String origem;
    private String destino;
    private Double valor;
}
