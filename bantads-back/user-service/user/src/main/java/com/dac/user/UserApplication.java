package com.dac.user;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dac.user.models.EnderecoModel;
import com.dac.user.models.UserModel;
import com.dac.user.rabbit.Producer;
import com.dac.user.repository.UserRepository;

import shared.Message;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Autowired UserRepository userRepository;
	@Autowired Producer producer;

	@Bean
	CommandLineRunner runner() {
		return args -> {
			userRepository.save(new UserModel("111", "cliente1@email.com", 
				"CLIENTE 1", "9999-2222", 3000.00, 
				null));

			userRepository.save(new UserModel("222", "cliente2@email.com",
				"CLIENTE 1", "9999-2222", 3000.00, 
				null));

			userRepository.save(new UserModel("333", "cliente3@email.com",
				"CLIENTE 1", "9999-2222", 3000.00, 
				null));

			producer.sendMessage(new Message<>(
				UUID.randomUUID().toString(), "testConnection", 
				null, "conta", "cliente.response"));
			System.out.println(userRepository.findAll());	
		};
		
	}

}
