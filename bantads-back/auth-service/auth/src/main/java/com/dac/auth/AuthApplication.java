package com.dac.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;


@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@Autowired AuthRepository repo;

	@Bean
	CommandLineRunner run(){
		return args -> {
			repo.save(new AuthModel("1","email1@example.com","123","CLIENTE"));
			repo.save(new AuthModel("2","email2@example.com","1234","CLIENTE"));
			repo.save(new AuthModel("3","email3@example.com","1235","CLIENTE"));
			System.out.println(this.repo.findAll());
		};
	}

}
