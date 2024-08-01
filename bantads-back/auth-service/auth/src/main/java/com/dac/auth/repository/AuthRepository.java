package com.dac.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.*;

import com.dac.auth.model.AuthModel;

public interface AuthRepository extends MongoRepository<AuthModel, String> {
    Optional <AuthModel> findByEmail(String email);

    boolean existsByEmail(String email);
    

    boolean deleteByEmail(String email);
}