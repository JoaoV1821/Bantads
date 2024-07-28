package com.dac.user.service;

import com.dac.user.models.UserModel;

public interface UserService {
    UserModel findByEmail(String email);

    UserModel create(UserModel user);

    void atualizar(String cpf,UserModel user);

    Boolean deletarPorId(String id);
}
