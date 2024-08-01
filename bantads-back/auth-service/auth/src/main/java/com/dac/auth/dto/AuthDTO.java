package com.dac.auth.dto;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
@Getter
@Setter
public class AuthDTO {
    @Id
    private String email;
    private String senha;
    private String tipo;
    private boolean active;
    private String salt;
}
