package ms.conta.models.dto;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import ms.conta.models.enums.MovimentacaoEnum;

@Data
@AllArgsConstructor
public class MovimentacaoDTO {
    private Long id;
    private DateTimeFormat data;
    private MovimentacaoEnum tipo;
    private Long origem;
    private Long destino;
    private Double valor;
}
