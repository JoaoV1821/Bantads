package com.dac.auth.repository;

import org.springframework.data.mongodb.repository.*;

import com.dac.auth.model.AuthModel;

public interface AuthRepository extends MongoRepository<AuthModel, String> {

    public Boolean existsByEmail(String email);
    public void deleteByEmail(String email);
    
}