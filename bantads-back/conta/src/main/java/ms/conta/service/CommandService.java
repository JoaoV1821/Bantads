package ms.conta.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.conta.ContaApplication;
import ms.conta.models.Conta;
import ms.conta.models.Movimentacao;
import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.models.dto.QueryUpdateDTO;
import ms.conta.models.dto.RejeicaoDTO;
import ms.conta.rabbit.Producer;
import ms.conta.repository.commandrepository.CommandRepository;
import ms.conta.repository.commandrepository.MovimentacaoCommandRepository;
import ms.conta.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;
import shared.dtos.GerenteDTO;

@Service
public class CommandService {
    
    @Autowired private Producer producer;
    @Autowired private QueryService queryService;

    @Autowired private CommandRepository contaRepository;
    @Autowired private MovimentacaoCommandRepository movimentacaoRepository;

    public ContaDTO atualizar(ContaDTO conta){
        Optional<Conta> old = queryService.buscarPorId(conta.getId());
        if (!old.isPresent()) {
            return null;
        }
        Conta atualizada = old.get();
        atualizada.setEstado(conta.getEstado());
        atualizada.setLimite(conta.getLimite());
        atualizada.setId_gerente(conta.getId_gerente());

        ContaDTO salva = Transformer.transform(this.contaRepository.save(atualizada), ContaDTO.class);
        queryUpdate(salva, "updateAccount");

        return salva;
    }

    public ContaDTO atualizarPorId_cliente(ContaDTO conta){
        Optional<Conta> old = queryService.buscarPorId_cliente(conta.getId_cliente());
        if (!old.isPresent()) {
            return null;
        }
        Conta atualizada = old.get();
        atualizada.setEstado(conta.getEstado());
        atualizada.setLimite(conta.getLimite());
        atualizada.setId_gerente(conta.getId_gerente());
        
        ContaDTO salva = Transformer.transform(this.contaRepository.save(atualizada), ContaDTO.class);
        queryUpdate(salva, "updateAccount");

        return salva;
    }

    public ContaDTO atualizarPorId_gerente(List<String> ids){
        //List[0] = novo id
        //List[1] = antigo id
        Optional<Conta> old = queryService.buscarPorId_gerente(ids.get(1));
        if (!old.isPresent()) {
            return null;
        }
        Conta atualizada = old.get();
        atualizada.setId_gerente(ids.get(0));
        
        ContaDTO salva = Transformer.transform(this.contaRepository.save(atualizada), ContaDTO.class);
        queryUpdate(salva, "updateAccount");

        return salva;
    }

    public Integer atualizarGerente(List<String> ids) {
        // List[0] = novo id
        // List[1] = antigo id
        List<Conta> oldGerenteAccounts = contaRepository.findAllByIdgerente(ids.get(1));
        System.out.println("CONTA REPOSITORY FIND ALL");
        System.out.println(contaRepository.findAll());
        if (oldGerenteAccounts.isEmpty()) {
            return -1;
        }
    
        for (Conta account : oldGerenteAccounts) {
            account.setId_gerente(ids.get(0));
        }
        
        contaRepository.saveAll(oldGerenteAccounts);
        
        List<Conta> updatedAccounts = contaRepository.findAllByIdgerente(ids.get(0));
        for (Conta account : updatedAccounts) {
            queryUpdate(Transformer.transform(account, ContaDTO.class), "updateAccount");
        }
    
        return 1;
    }

    public ContaDTO atualizarLimite(ClienteDTO cliente){
        Optional<Conta> old = queryService.buscarPorId_cliente(cliente.getUuid());
        if (!old.isPresent()) {
            return null;
        }
        Conta atualizada = old.get();

        Double novoLimite = cliente.getSalario() > 2000 ? cliente.getSalario() / 2 : 0.00;

        if(novoLimite < atualizada.getSaldo())
            novoLimite = atualizada.getSaldo();

        atualizada.setLimite(novoLimite);
        
        ContaDTO salva = Transformer.transform(this.contaRepository.save(atualizada), ContaDTO.class);
        queryUpdate(salva, "updateAccount");

        return salva;
    }

    public ContaDTO salvar(ContaDTO conta){
        if (conta == null) return null;
        
        Conta salva = this.contaRepository.save(Transformer.transform(conta, Conta.class));
        ContaDTO contaDTO = Transformer.transform(salva, ContaDTO.class);
        queryUpdate(contaDTO, "saveAccount");

        return contaDTO;
    }

    public Boolean deletarPorId(Long id) {
        if (!contaRepository.existsById(id)) {
            return false;
        }

        contaRepository.deleteById(id);
        if (!contaRepository.existsById(id)) {
            queryUpdate(id, "deleteAccount");
            return true;
        }
        return false;
    }

    public MovimentacaoDTO saque(Long origem, MovimentacaoDTO dto){
        Conta conta = queryService.buscarPorId(origem).orElseThrow(NoSuchElementException::new);
        if (dto.getValor() > conta.getSaldo()) {
            throw new IllegalArgumentException("Sem fundos suficientes");
        }
        conta.setSaldo(conta.getSaldo() - dto.getValor());
        this.contaRepository.save(conta);
        Movimentacao salvo = this.movimentacaoRepository.save(Transformer.transform(dto, Movimentacao.class));
        
        MovimentacaoDTO movimentacaoDTO = Transformer.transform(salvo, MovimentacaoDTO.class);
        
        var contaDTO = Transformer.transform(conta, ContaDTO.class);
        QueryUpdateDTO queryUpdateDTO = new QueryUpdateDTO(contaDTO, null, movimentacaoDTO);

        queryUpdate(queryUpdateDTO, "saveMovement");

        return movimentacaoDTO;
    }

    public MovimentacaoDTO deposito(Long origem, MovimentacaoDTO dto){
        Conta conta = queryService.buscarPorId(origem).orElseThrow(NoSuchElementException::new);
        conta.setSaldo(conta.getSaldo() + dto.getValor());
        this.contaRepository.save(conta);
        Movimentacao salvo = this.movimentacaoRepository.save(Transformer.transform(dto, Movimentacao.class));
        
        MovimentacaoDTO movimentacaoDTO = Transformer.transform(salvo, MovimentacaoDTO.class);
        
        var contaDTO = Transformer.transform(conta, ContaDTO.class);
        QueryUpdateDTO queryUpdateDTO = new QueryUpdateDTO(contaDTO, null, movimentacaoDTO);

        queryUpdate(queryUpdateDTO, "saveMovement");

        return movimentacaoDTO;
    }

    public MovimentacaoDTO transferencia(Long origem, MovimentacaoDTO dto){
        Conta contaOrigem = queryService.buscarPorId(origem).orElseThrow(NoSuchElementException::new);
        Conta contaDestino = queryService.buscarPorId(dto.getDestino()).orElseThrow(NoSuchElementException::new);

        if (dto.getValor() > contaOrigem.getSaldo()) {
            throw new IllegalArgumentException("Sem fundos suficientes");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo() - dto.getValor());
        contaDestino.setSaldo(contaDestino.getSaldo() + dto.getValor());

        this.contaRepository.save(contaOrigem);
        this.contaRepository.save(contaDestino);
        Movimentacao salvo = this.movimentacaoRepository.save(Transformer.transform(dto, Movimentacao.class));
        
        MovimentacaoDTO movimentacaoDTO = Transformer.transform(salvo, MovimentacaoDTO.class);
        
        var contaOrigemDTO = Transformer.transform(contaOrigem, ContaDTO.class);
        var contaDestinoDTO = Transformer.transform(contaDestino, ContaDTO.class);
        QueryUpdateDTO queryUpdateDTO = new QueryUpdateDTO(contaOrigemDTO, contaDestinoDTO, movimentacaoDTO);

        queryUpdate(queryUpdateDTO, "saveMovement");
        
        return movimentacaoDTO;
    }

    public Pair<ClienteDTO,ContaDTO> rejeitarCliente(String id, RejeicaoDTO rejeicao){
        Optional<Conta> conta = queryService.buscarPorId_cliente(id);
        if(!conta.isPresent()){
            return null;
        }
        Conta buscado = conta.get();
        buscado.setEstado(0);
        buscado.setData(rejeicao.getData_rejeicao());

        contaRepository.save(buscado);
        ContaDTO buscadoDTO = Transformer.transform(buscado, ContaDTO.class);

        //REQUISITAR CLIENTE REJEITADO
        GenericData<ClienteDTO> clienteData = new GenericData<ClienteDTO>();
        ClienteDTO cli = new ClienteDTO();
        cli.setUuid(buscadoDTO.getId_cliente());
        clienteData.setDto(cli);
        Message<ClienteDTO> msgOld = new Message<ClienteDTO>(UUID.randomUUID().toString(),
        "requestClient", clienteData , "cliente", "conta.response");
        
        Mono<GenericData<?>> clienteResponse = producer.sendRequest(msgOld);
 
        ClienteDTO clienteDTO = clienteResponse.map(response -> {
            GenericData<ClienteDTO> cliente = Transformer.transform(response, GenericData.class);
            System.out.println("cliente " + cliente);
            return cliente.getDto();
        }).block();

        if(clienteDTO == null) return null;

        return new Pair<ClienteDTO, ContaDTO>(clienteDTO, buscadoDTO);

    }

    public List<MovimentacaoDTO> transformList(List<Movimentacao> list){
        return list.stream()
            .map(mov -> Transformer.transform(mov, MovimentacaoDTO.class))
            .collect(Collectors.toList());
    }

    public <T> void queryUpdate(T dto, String request){
        GenericData<T> data = new GenericData<>();
        data.setDto(dto);
        Message<T> msg = new Message<>( String.valueOf(ThreadLocalRandom.current().nextInt()),
            request, data , "query", null);
        producer.sendMessage(msg);
    }

}
