package ms.conta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ms.conta.repository.commandrepository.CommandRepository;
import ms.conta.repository.queryrepository.QueryRepository;

@SpringBootApplication
public class ContaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContaApplication.class, args);
	}

	@Autowired CommandRepository commandRepository;
	@Autowired QueryRepository queryRepository;

	@Bean 
	CommandLineRunner run(){
		return args -> {
			System.out.println(commandRepository.findAll());
			System.out.println(queryRepository.findAll());
		};
	}

}
