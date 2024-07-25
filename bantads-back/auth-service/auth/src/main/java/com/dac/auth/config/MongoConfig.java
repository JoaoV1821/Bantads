package com.dac.auth.config;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableMongoRepositories(basePackageClasses = AuthRepository.class)
@Configuration
public class MongoConfig {
    @Bean
    CommandLineRunner commandLineRunner(AuthRepository authRepository) {
        return strings -> {
            

            AuthModel cliente = new AuthModel(UUID.randomUUID().toString(),"email_daora@gmail.com", "123456" , "CLIENTE");
            AuthModel gerente = new AuthModel(UUID.randomUUID().toString(),"email@gerente.com", "123456", "GERENTE");
            AuthModel admin = new AuthModel(UUID.randomUUID().toString(),"admin@admin.com", "admin", "ADMIN");

            authRepository.save(cliente);
            authRepository.save(gerente);
            authRepository.save(admin);
            
        };
    }

}
