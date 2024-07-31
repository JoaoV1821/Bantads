package ms.conta.models.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.dtos.ContaDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryUpdateDTO implements Serializable{
    
    ContaDTO origem;
    ContaDTO destino;
    MovimentacaoDTO movimentacao;

}
