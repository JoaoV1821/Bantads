package com.dac.user.service;

import com.dac.user.models.UserModel;

import java.util.Optional;

public interface UserService {
    boolean findByEmail(String email);

    Optional<UserModel> findById(String id);

    UserModel create(UserModel user);

    void atualizar(String id, UserModel user);
    
    void delete(String id);

    Boolean deletarPorId(String id);
}
