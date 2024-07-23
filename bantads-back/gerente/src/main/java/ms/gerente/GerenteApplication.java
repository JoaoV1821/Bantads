package ms.gerente;

import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ms.gerente.rabbit.Producer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;

@SpringBootApplication
public class GerenteApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(GerenteApplication.class, args);
	}

	@Autowired private GerenteRepository repo;
	
	@Autowired private Producer producer;

	@Override
	public void run(String ...args) throws Exception{
		repo.save(new Gerente(1L, "000", "gerente1@email.com", "9999-9999"));
		repo.save(new Gerente(2L, "111", "gerente2@email.com", "9998-8888"));
		repo.save(new Gerente(3L, "222", "gerente3@email.com", "9997-7777"));
		
		producer.sendMessage(new Message<>(
				UUID.randomUUID().toString(), "testConnection", 
				null, "cliente", "gerente.response"));
		System.out.println(repo.findAll());	

		Message<ClienteDTO> msg = new Message<>(UUID.randomUUID().toString(), 
        "listAll", null, "cliente", "gerente.response");    
        
        producer.sendRequest(msg)
                .subscribe(response -> System.out.println("response" + response));
    }

}
