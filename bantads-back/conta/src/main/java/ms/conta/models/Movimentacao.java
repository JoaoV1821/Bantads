package ms.conta.models;

import org.springframework.format.annotation.DateTimeFormat;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private DateTimeFormat data;
    private MovimentacaoEnum tipo;
    private Long origem;
    private Long destino;
    private Double valor;
}
