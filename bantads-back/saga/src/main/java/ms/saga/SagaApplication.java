package ms.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ms.saga.autocadastro.AutocadastroService;
import ms.saga.dtos.OrchestratorRequestDTO;
import ms.saga.dtos.OrchestratorResponseDTO;
import ms.saga.rabbit.Producer;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SagaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SagaApplication.class, args);
	}

	@Autowired AutocadastroService autocadastroService;
	@Autowired Producer producer;

	@Bean
	CommandLineRunner run(){
		return args -> {
		/* 
		Mono<GenericData<?>> responseGerente = producer.sendRequest(new Message<>(UUID.randomUUID().toString()
		, "requestManagerForNewAccount", null, "gerente", "saga.response"));
		responseGerente.subscribe(
			response -> System.out.println(response),
			error -> System.err.println("Error: " + error.getMessage())
		);
		};
		*/

		OrchestratorRequestDTO requestDTO = new OrchestratorRequestDTO();
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
        Mono<OrchestratorResponseDTO> responseMono = autocadastroService.autocadastro(requestDTO);
        responseMono.subscribe(
            response -> System.out.println("Autocadastro Response: " + response),
            error -> System.err.println("Error: " + error.getMessage())
        );
		};
	}

}
