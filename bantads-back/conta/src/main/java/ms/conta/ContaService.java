package ms.conta;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.conta.models.Conta;
import ms.conta.models.Movimentacao;
import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.util.Transformer;
import shared.dtos.ContaDTO;

@Service
public class ContaService {
    
    @Autowired private ContaRepository contaRepository;
    @Autowired private MovimentacaoRepository movimentacaoRepository;

    public List<ContaDTO> listar(){
        return this.contaRepository.findAll().stream()
        .map(conta -> Transformer.transform(conta, ContaDTO.class))
        .collect(Collectors.toList());
    }

    public Optional<Conta> buscarPorId(Long id){
        return this.contaRepository.findById(id);
    }

    public MovimentacaoDTO saque(Long origem, MovimentacaoDTO dto){
        Conta conta = this.buscarPorId(origem).orElseThrow(NoSuchElementException::new);
        if(dto.getValor() > conta.getSaldo()){
            throw new IllegalArgumentException("Sem fundos suficientes");
        }
        conta.setSaldo(conta.getSaldo() - dto.getValor());
        this.contaRepository.save(conta);
        Movimentacao salvo = this.movimentacaoRepository.save(Transformer.transform(dto, Movimentacao.class));
        return Transformer.transform(salvo, MovimentacaoDTO.class);
    }

    public MovimentacaoDTO deposito(Long origem, MovimentacaoDTO dto){
        Conta conta = this.buscarPorId(origem).orElseThrow(NoSuchElementException::new);
        conta.setSaldo(conta.getSaldo() + dto.getValor());
        this.contaRepository.save(conta);
        Movimentacao salvo = this.movimentacaoRepository.save(Transformer.transform(dto, Movimentacao.class));
        return Transformer.transform(salvo, MovimentacaoDTO.class);
    }

    public MovimentacaoDTO transferencia(Long origem, MovimentacaoDTO dto){
        Conta contaOrigem = this.buscarPorId(origem).orElseThrow(NoSuchElementException::new);
        Conta contaDestino = this.buscarPorId(dto.getDestino()).orElseThrow(NoSuchElementException::new);

        if(dto.getValor() > contaOrigem.getSaldo()){
            throw new IllegalArgumentException("Sem fundos suficientes");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo() - dto.getValor());
        contaDestino.setSaldo(contaDestino.getSaldo() + dto.getValor());

        this.contaRepository.save(contaOrigem);
        this.contaRepository.save(contaDestino);
        Movimentacao salvo = this.movimentacaoRepository.save(Transformer.transform(dto, Movimentacao.class));
        return Transformer.transform(salvo, MovimentacaoDTO.class);
    }

    public List<MovimentacaoDTO> extrato(Long id, Date dataInicial, Date dataFinal){
        
        List<Movimentacao> movimentacao;
        
        if(dataInicial != null && dataFinal == null){
            movimentacao = this.movimentacaoRepository.findByDataInicial(id, dataInicial);
            return transformList(movimentacao);
        }
        if (dataInicial != null && dataFinal != null){
            movimentacao = this.movimentacaoRepository.findByDataInicialEDataFinal(id, dataInicial, dataFinal);
            return transformList(movimentacao);
        }
        
        movimentacao = this.movimentacaoRepository.findAll();
        return transformList(movimentacao);

    }

    public List<MovimentacaoDTO> transformList(List<Movimentacao> list){
        return list.stream()
            .map(mov -> Transformer.transform(mov, MovimentacaoDTO.class))
            .collect(Collectors.toList());
    }
}
