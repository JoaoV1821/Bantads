package ms.gerente;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gerente {

    @Id
    private String id;
    private String cpf;
    private String email;
    private String telefone;

}
