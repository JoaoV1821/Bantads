package shared.dtos;

import lombok.Data;

@Data
public class RelatorioClientesDTO {
    
    private GerenteDTO gerente;
    private ContaDTO conta;
    private ClienteDTO cliente;
}
