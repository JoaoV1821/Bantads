package com.dac.user.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserModel implements Serializable {
    
    @Id
    private String uuid;
    private String cpf;
    private String email;
    private String nome;
    private double salario;
    private String telefone;
    private int estado;
    
    private String tipo;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String uf;

    
}
