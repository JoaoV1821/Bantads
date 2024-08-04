package shared.dtos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelaInicialDTO implements Serializable{
    
    public String id_gerente;
    public Long numeroClientes;
    public Double saldoPositivoAgg;
    public Double saldoNegativoAgg;

}
