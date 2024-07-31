package com.dac.user.mapper;

import java.util.Optional;

import com.dac.user.dto.UserDTO;
import com.dac.user.models.UserModel;
import com.dac.user.utils.Transformer;

public class UserMapper {

    public static UserDTO toDto(Optional<UserModel> userModel) {
        return Transformer.transform(userModel, UserDTO.class);
    }
            
}
