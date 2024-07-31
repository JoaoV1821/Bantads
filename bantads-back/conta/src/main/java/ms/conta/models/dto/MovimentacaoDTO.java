package ms.conta.models.dto;

import java.io.Serializable;
import java.sql.Date;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ms.conta.models.enums.MovimentacaoEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoDTO implements Serializable{
    private String id;
    private Date data;
    private MovimentacaoEnum tipo;
    private String origem;
    private String destino;
    private Double valor;
}
