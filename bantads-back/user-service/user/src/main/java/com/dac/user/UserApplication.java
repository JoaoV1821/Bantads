package com.dac.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dac.user.repository.UserRepository;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Autowired UserRepository repo;

	@Bean
	CommandLineRunner run() {
		return args -> {
			System.out.println(repo.findAll());
		};
	}

}
