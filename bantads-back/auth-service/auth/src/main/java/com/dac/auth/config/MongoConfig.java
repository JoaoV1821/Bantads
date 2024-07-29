package com.dac.auth.config;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


import com.dac.auth.repository.AuthRepository;
import org.springframework.context.annotation.Configuration;

@EnableMongoRepositories(basePackageClasses = AuthRepository.class)
@Configuration
public class MongoConfig {
    

}
