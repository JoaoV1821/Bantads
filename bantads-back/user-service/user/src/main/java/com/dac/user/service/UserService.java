package com.dac.user.service;

import com.dac.user.models.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    boolean findByEmail(String email);

    Optional<UserModel> findByUUID(UUID uuid);

    UserModel create(UserModel user);

    void atualizar(UUID uuid, UserModel user);
    
    void delete(UUID uuid);
}
