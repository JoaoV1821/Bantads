package com.dac.user.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class EnderecoModel implements Serializable {
    private String tipo;
    private String logradouro;
    private int numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String estado;

    EnderecoModel() {
        super();
    }
}
