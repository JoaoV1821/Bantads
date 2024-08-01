package com.dac.user.dto;

import java.util.UUID;

import com.dac.user.models.EnderecoModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class UserDTO {  
    
    private UUID uuid;
    private String cpf;
    private String email;
    private String nome;
    private double salario;
    private String telefone;
    private int estado;
    private EnderecoModel endereco;    
}
