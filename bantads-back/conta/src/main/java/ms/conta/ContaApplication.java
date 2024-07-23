package ms.conta;

import java.sql.Date;
import java.time.LocalDate;
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
			contaRepository.save(new Conta(1L, 100.00, 999.00, Date.valueOf(LocalDate.now()), 
				 1L, "111", "CLIENTE 1",
				 1L, "111", "GERENTE 1"));
			contaRepository.save(new Conta(2L, 1000.00, 999.00, Date.valueOf(LocalDate.now()), 
				 2L, "222", "CLIENTE 2",
				 2L, "222", "GERENTE 2"));
			contaRepository.save(new Conta(3L, 10000.00, 999.00, Date.valueOf(LocalDate.now()), 
				 1L, "333", "CLIENTE 3",
				 1L, "333", "GERENTE 3"));
			producer.sendMessage(new Message<>(
				UUID.randomUUID().toString(), "testConnection", 
				null, "gerente", "conta.response"));
			System.out.println(contaRepository.findAll());	
		};
		
	}

}
