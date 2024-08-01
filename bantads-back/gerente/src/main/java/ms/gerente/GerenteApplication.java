package ms.gerente;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Data;
import ms.gerente.rabbit.Producer;
import ms.gerente.util.Transformer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;
import shared.dtos.GerenteDTO;

@SpringBootApplication
public class GerenteApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(GerenteApplication.class, args);
	}

	@Autowired private GerenteRepository repo;
	@Autowired private GerenteService service;
	@Autowired private Producer producer;

	private List<ClienteDTO> clientes = new ArrayList<>();
	private List<GerenteDTO> gerentes = new ArrayList<>();
	private List<ContaDTO> contas = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	@Override
	public void run(String ...args) throws Exception{
		repo.save(new Gerente("1", "000", "gerente1@email.com", "9999-9999"));
		repo.save(new Gerente("2", "111", "gerente2@email.com", "9998-8888"));
		repo.save(new Gerente("3", "222", "gerente3@email.com", "9997-7777"));
		System.out.println(this.repo.findAll());

		producer.sendMessage(new Message<>(
				UUID.randomUUID().toString(), "testConnection", 
				null, "cliente", "gerente.response"));
		
		Message<ClienteDTO> msg = new Message<>(UUID.randomUUID().toString(), 
        "listAll", null, "cliente", "gerente.response");    
		
        producer.sendRequest(msg)
            .map(response -> {
                GenericData<ClienteDTO> data = Transformer.transform(response, GenericData.class);
				clientes = data.getList();
                return clientes;
            })
            .block();

		Message<ContaDTO> msgConta = new Message<>(UUID.randomUUID().toString(), 
			"listAll", null, "conta", "gerente.response");    

		producer.sendRequest(msgConta)
            .map(response -> {
                GenericData<ContaDTO> data = Transformer.transform(response, GenericData.class);
				contas = data.getList();
                return contas;
            })
            .block();

		gerentes = this.service.listar().stream().map(g -> Transformer.transform(g, GerenteDTO.class)).collect(Collectors.toList());

		Map<String, GerenteDTO> gerenteMap = gerentes.stream()
			.collect(Collectors.toMap(
				GerenteDTO::getId,
				gerente -> gerente
    		));
/* 
		List<MergedObject> merged = contas.stream()
		.map(a -> {
			gerente = gerenteMap.get(a.getId_gerente)
			return new MergedObject(set atributos<conta> set atributos<gerente>))
			}
		.collect(Collectors.toList());
*/		
		List<Pair<GerenteDTO, ContaDTO>> joined = contas.stream()
			.map(a -> new Pair(gerenteMap.get(a.getId_gerente()), a))
			.collect(Collectors.toList());

		System.out.println(joined);
    }
 
	@Data
	class Pair<T, R> {
		private T t;
		private R r;
		Pair(T t, R r){
			this.t = t;
			this.r = r;
		}
	}

}