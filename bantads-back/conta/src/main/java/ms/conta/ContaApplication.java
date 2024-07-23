package ms.conta;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ms.conta.rabbit.Producer;
import shared.Message;

@SpringBootApplication
public class ContaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContaApplication.class, args);
	}

	@Autowired Producer producer;

	@Bean
	CommandLineRunner runner() {
		return args -> {
			producer.sendMessage(new Message<>(
				UUID.randomUUID().toString(), "request", 
				null, "gerente", "conta.response"));
		};
		
	}

}
