package com.dac.auth.mapper;

import com.dac.auth.dto.AuthDTO;
import com.dac.auth.model.AuthModel;

public class AuthMapper {
    public static AuthDTO toDto(AuthModel authModel) {
        return new AuthDTO(authModel.getEmail(), authModel.getSenha(), authModel.getTipo());
    }
}
