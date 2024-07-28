package ms.conta;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ms.conta.models.Conta;
import ms.conta.rabbit.Producer;
import shared.Message;

@SpringBootApplication
public class ContaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContaApplication.class, args);
	}

	@Autowired ContaRepository contaRepository;
	@Autowired Producer producer;

	@Bean
	CommandLineRunner runner() {
		return args -> {
			contaRepository.save(new Conta(UUID.randomUUID().toString(), 100.00, 999.00, Date.valueOf(LocalDate.now()), 
				 "1", "1", 0));
			contaRepository.save(new Conta(UUID.randomUUID().toString(), 1000.00, 999.00, Date.valueOf(LocalDate.now()), 
				 "2", "1", 0));
			contaRepository.save(new Conta(UUID.randomUUID().toString(), 10000.00, 999.00, Date.valueOf(LocalDate.now()), 
				 "3", "2", 0));
			producer.sendMessage(new Message<>(
				UUID.randomUUID().toString(), "testConnection", 
				null, "gerente", "conta.response"));
			
			System.out.println(contaRepository.findAll());	
		};
		
	}
}
