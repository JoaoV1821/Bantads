package com.dac.auth.model;
import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Document
@Data
@Getter
@Setter
@AllArgsConstructor
public class AuthModel implements Serializable {
    @Id
    private String uuid;
    private String email;
    private String senha;
    private String tipo;
    private boolean active;
    private String salt;

    public AuthModel() {
        super();
        
    }

    
}
