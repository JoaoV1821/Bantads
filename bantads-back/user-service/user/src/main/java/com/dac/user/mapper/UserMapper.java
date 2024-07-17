package com.dac.user.mapper;

import com.dac.user.dto.UserDTO;
import com.dac.user.models.UserModel;

public class UserMapper {

    public static UserDTO toDto(UserModel userModel) {
        return new UserDTO(userModel.getEmail(), userModel.getNome(), userModel.getCpf(), userModel.getSalario(), userModel.getTelefone(), userModel.getEndereco());
    }
    
}
