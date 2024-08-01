package ms.conta.models.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import ms.conta.models.enums.MovimentacaoEnum;

@Data
@AllArgsConstructor
public class MovimentacaoDTO {
    private Long id;
    private Date data;
    private MovimentacaoEnum tipo;
    private String origem;
    private String destino;
    private Double valor;
}
