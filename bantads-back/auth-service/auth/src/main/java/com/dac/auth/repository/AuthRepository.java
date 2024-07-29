package com.dac.auth.repository;

import org.springframework.data.mongodb.repository.*;

import com.dac.auth.model.AuthModel;

public interface AuthRepository extends MongoRepository<AuthModel, String> {

    
}