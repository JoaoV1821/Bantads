package ms.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ms.saga.alteracaodeperfil.UpdateProfileService;
import ms.saga.autocadastro.AutocadastroService;
import ms.saga.dtos.AutocadastroRequestDTO;
import ms.saga.dtos.AutocadastroResponseDTO;
import ms.saga.dtos.UpdateProfileRequestDTO;
import ms.saga.dtos.UpdateProfileResponseDTO;
import ms.saga.rabbit.Producer;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SagaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SagaApplication.class, args);
	}

	@Autowired UpdateProfileService service;
	@Autowired Producer producer;

	@Bean
	CommandLineRunner run(){
		return args -> {

		UpdateProfileRequestDTO requestDTO = new UpdateProfileRequestDTO();
		requestDTO.setId("client1");
		requestDTO.setNome("André");
		requestDTO.setEmail("aalexjankoski@gmail.com");
		requestDTO.setCpf("10123415955");
		requestDTO.setTelefone("41995448887");
		requestDTO.setSalario(3200.00);
		requestDTO.setLogradouro("Rua Alcides Vieira Arcoverde");
		requestDTO.setNumero("1225");
		requestDTO.setComplemento("SEPT");
		requestDTO.setCep("80000000");
		requestDTO.setCidade("Curitiba");
		requestDTO.setUf("PR");
		
		//Vai vir do controller 
        Mono<UpdateProfileResponseDTO> responseMono = service.updateProfile(requestDTO);
        responseMono.subscribe(
            response -> System.out.println("UpdateProfile Response: " + response),
            error -> System.err.println("Error: " + error.getMessage())
        );
		};
	}

}
