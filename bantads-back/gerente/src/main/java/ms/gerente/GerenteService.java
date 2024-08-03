package ms.gerente;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.gerente.rabbit.Producer;
import ms.gerente.util.Transformer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;
import shared.dtos.GerenteDTO;
import shared.dtos.RelatorioClientesDTO;
import shared.dtos.TelaInicialDTO;

@Service
public class GerenteService {
    
    @Autowired
    private GerenteRepository gerenteRepository;
    @Autowired 
    private Producer producer;

    public List<GerenteDTO> listar(){
        return this.gerenteRepository.findAll().stream()
            .map(gerente -> Transformer.transform(gerente, GerenteDTO.class))
            .collect(Collectors.toList());
    }

    public GerenteDTO buscarPorId(String id){
        Optional<Gerente> salvo =  this.gerenteRepository.findById(id);
        if(salvo.isPresent()){
            return Transformer.transform(salvo, GerenteDTO.class);
        }
        return null;
    }

    public GerenteDTO salvar(GerenteDTO dto) {
        //TODO RELACIONAR CONTAS

        Gerente gerente = Transformer.transform(dto, Gerente.class);
        Gerente savedGerente = this.gerenteRepository.save(gerente);
        return Transformer.transform(savedGerente, GerenteDTO.class);
    }

    public GerenteDTO atualizar(String id, GerenteDTO dto) {
        GerenteDTO oldGerente = this.buscarPorId(id);
        if(oldGerente == null) return null;

        oldGerente.setEmail(dto.getEmail());
        oldGerente.setTelefone(dto.getTelefone());
        Gerente salvo = this.gerenteRepository.save(Transformer.transform(oldGerente, Gerente.class));
        return Transformer.transform(salvo, GerenteDTO.class);
    }

    public Boolean remover(String id){
        //TODO RELACIONAR CONTAS
        
        if(this.listar().size() <= 1){ return false; }
        if (!gerenteRepository.existsById(id)) { return false; }

        this.gerenteRepository.deleteById(id);
        return !gerenteRepository.existsById(id);
        
    }

    public List<Pair<GerenteDTO, TelaInicialDTO>> telaInicial(){
        
        //PUXAR CONTAS POR GERENTE
        Message msgConta = new Message<>(UUID.randomUUID().toString(), 
			"requestAllAccountsByManager", null, "conta", "gerente.response");    

		List<TelaInicialDTO> contas = producer.sendRequest(msgConta)
            .map(response -> {
                @SuppressWarnings("unchecked")
                GenericData<TelaInicialDTO> dataResponse = Transformer.transform(response, GenericData.class);
				return dataResponse.getList();
            })
            .block();

        if(contas == null) return null;

        List<Pair<GerenteDTO, TelaInicialDTO>> lista = new ArrayList<>();
        //PUXAR GERENTES QUE CONSTAM NA LISTA
        for (TelaInicialDTO conta : contas) {
            GerenteDTO buscado = buscarPorId(conta.getId_gerente());
            if (buscado != null) {
                lista.add(new Pair<GerenteDTO, TelaInicialDTO>(buscado, conta));
            }
        }

        return lista;
        
    }

    public List<RelatorioClientesDTO> relatorioClientes(){
        
        //PUXAR CONTAS
        Message msgConta = new Message<>(UUID.randomUUID().toString(), 
			"listAll", null, "conta", "gerente.response");    

		List<ContaDTO> contas = producer.sendRequest(msgConta)
            .map(response -> {
                @SuppressWarnings("unchecked")
                GenericData<ContaDTO> dataResponse = Transformer.transform(response, GenericData.class);
				return dataResponse.getList();
            })
            .block();

        if(contas == null) return null;

        //PUXAR CLIENTES
        Message msgCliente = new Message<>(UUID.randomUUID().toString(), 
			"listAll", null, "cliente", "gerente.response"); 

        List<ClienteDTO> clientes = producer.sendRequest(msgCliente)
            .map(response -> {
                @SuppressWarnings("unchecked")
                GenericData<ClienteDTO> dataResponse = Transformer.transform(response, GenericData.class);
				return dataResponse.getList();
            })
            .block();

        if(clientes == null) return null;

        Map<String, ClienteDTO> clienteMap = clientes.stream()
            .collect(Collectors.toMap(
                ClienteDTO::getUuid,
                cliente -> cliente));

        List<RelatorioClientesDTO> relatorioClientes = new ArrayList<>();

        for (ContaDTO conta : contas) {
            ClienteDTO cliente = clienteMap.get(conta.getId_cliente());
            if (cliente != null) {
                RelatorioClientesDTO relatorio = new RelatorioClientesDTO();
                relatorio.setConta(conta);
                relatorio.setCliente(cliente);
                relatorio.setGerente(buscarPorId(conta.getId_gerente()));
                relatorioClientes.add(relatorio);
                }
        }

    return relatorioClientes;
        
    }
    
}