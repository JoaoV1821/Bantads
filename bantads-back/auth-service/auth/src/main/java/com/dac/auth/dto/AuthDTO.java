package com.dac.auth.dto;



import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Document
@Getter
@Setter
@Data
public class AuthDTO {

    private String id;
    private String email;
    private String senha;
    private String tipo;
    private boolean active;
}
