package com.dac.user.models;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor

public class UserModel implements Serializable {
    
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

    UserModel() {
        super();
    }
    
}
