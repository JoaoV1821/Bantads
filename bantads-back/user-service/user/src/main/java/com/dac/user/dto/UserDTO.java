package com.dac.user.dto;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.dac.user.models.EnderecoModel;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class UserDTO {  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
  
    private UUID uuid;
    private String cpf;
    private String email;
    private String nome;
    private double salario;
    private String telefone;
    private int estado;
    private EnderecoModel endereco;    
}
