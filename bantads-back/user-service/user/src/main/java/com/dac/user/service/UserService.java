package com.dac.user.service;

import com.dac.user.models.UserModel;

import java.util.Optional;

public interface UserService {
   Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findByUUID(String uuid);

    UserModel create(UserModel user);

    void atualizar(String uuid, UserModel user);
    
    boolean delete(String uuid);
}
