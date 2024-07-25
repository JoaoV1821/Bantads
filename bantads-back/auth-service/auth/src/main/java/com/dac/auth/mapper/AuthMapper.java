package com.dac.auth.mapper;

import com.dac.auth.model.AuthModel;

import shared.dtos.AuthDTO;

public class AuthMapper {
    public static AuthDTO toDto(AuthModel authModel) {
        return new AuthDTO(authModel.getId(),authModel.getEmail(), authModel.getSenha(), authModel.getTipo());
    }
}
