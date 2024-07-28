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
    private String id;
    private String cpf;
    private String email;
    private String nome;
    private String telefone;
    private double salario;
    private EnderecoModel endereco;
    
}
