package ms.conta.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.conta.models.Conta;
import ms.conta.models.Movimentacao;
import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.models.dto.QueryUpdateDTO;
import ms.conta.rabbit.Producer;
import ms.conta.repository.commandrepository.CommandRepository;
import ms.conta.repository.commandrepository.MovimentacaoCommandRepository;
import ms.conta.util.Transformer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ContaDTO;

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

    public ContaDTO salvar(ContaDTO conta){
        if (conta == null) return null;
        Conta salva = this.contaRepository.save(Transformer.transform(conta, Conta.class));
        ContaDTO contaDTO = Transformer.transform(salva, ContaDTO.class);
        queryUpdate(contaDTO, "saveAccount");

        return contaDTO;
    }

    public Boolean deletarPorId(String id) {
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

    public MovimentacaoDTO saque(String origem, MovimentacaoDTO dto){
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

    public MovimentacaoDTO deposito(String origem, MovimentacaoDTO dto){
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

    public MovimentacaoDTO transferencia(String origem, MovimentacaoDTO dto){
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
