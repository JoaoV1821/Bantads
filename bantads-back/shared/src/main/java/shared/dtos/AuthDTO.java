package shared.dtos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO implements Serializable{
    
    private String id;
    private String email;
    private String senha;
    private String tipo;
    private boolean active;
    private String salt;
}
