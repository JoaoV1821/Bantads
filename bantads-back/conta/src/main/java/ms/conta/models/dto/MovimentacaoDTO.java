package ms.conta.models.dto;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import ms.conta.models.enums.MovimentacaoEnum;

@Data
@AllArgsConstructor
public class MovimentacaoDTO {
    private String id;
    private DateTimeFormat data;
    private MovimentacaoEnum tipo;
    private String origem;
    private String destino;
    private Double valor;
}
