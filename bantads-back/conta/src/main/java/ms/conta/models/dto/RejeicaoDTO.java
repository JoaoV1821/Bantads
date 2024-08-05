package ms.conta.models.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejeicaoDTO {
    
    private String motivo;
    private Date data_rejeicao;

}
