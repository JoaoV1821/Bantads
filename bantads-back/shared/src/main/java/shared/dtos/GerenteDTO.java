package shared.dtos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteDTO implements Serializable {
    
    private String id;
    private String cpf;
    private String email;
    private String telefone;

}
