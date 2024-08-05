package com.dac.user.dto;

import lombok.Data;

@Data
public class UserDTO {  
    
    private String uuid;
    private String cpf;
    private String email;
    private String nome;
    private double salario;
    private String telefone;
    
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String uf;
    
    private int estado; 
}
