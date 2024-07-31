package ms.conta.models;

import java.sql.Date;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ms.conta.models.enums.MovimentacaoEnum;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movimentacao {
    
    @Id
    private String id;
    private Date data;
    private MovimentacaoEnum tipo;
    private String origem;
    private String destino;
    private Double valor;
}
