package com.dac.auth.config;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.dac.auth.repository.AuthRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableMongoRepositories(basePackageClasses = AuthRepository.class)
@Configuration
public class MongoConfig {

  @Bean MongoClient mongoClient() {
      return MongoClients.create("mongodb://localhost:27017");
  }

  @Bean MongoOperations mongoTemplate(MongoClient mongoClient) {
      return new MongoTemplate(mongoClient, "auth");
  }
}

