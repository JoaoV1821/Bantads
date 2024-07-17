package com.dac.user.dto;

import org.springframework.data.annotation.Id;

import com.dac.user.models.EnderecoModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
@AllArgsConstructor
public class UserDTO {  
    @Id
    private String cpf;
    private String email;
    private String nome;
    private double salario;
    private String telefone;
    private EnderecoModel endereco;
    
}
