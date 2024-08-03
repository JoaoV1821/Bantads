package ms.conta.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.conta.models.Conta;
import ms.conta.models.Movimentacao;
import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.repository.queryrepository.QueryRepository;
import ms.conta.repository.queryrepository.MovimentacaoQueryRepository;
import ms.conta.util.Transformer;
import shared.dtos.ContaDTO;

@Service
public class QueryService {
    
    @Autowired private QueryRepository queryRepository;
    @Autowired private MovimentacaoQueryRepository movimentacaoRepository;

    public List<ContaDTO> listar(){
        return this.queryRepository.findAll().stream()
        .map(conta -> Transformer.transform(conta, ContaDTO.class))
        .collect(Collectors.toList());
    }

    public List<ContaDTO> listarPendentes(String id){
        return this.queryRepository.findByEstadoAndGroupByManager(id, 0).stream()
        .map(conta -> Transformer.transform(conta, ContaDTO.class))
        .collect(Collectors.toList());
    }

    public List<ContaDTO> listarPorGerente(String id){
        return this.queryRepository.listByGerente(id).stream()
        .map(conta -> Transformer.transform(conta, ContaDTO.class))
        .collect(Collectors.toList());
    }

    public List<ContaDTO> buscarTop3(String id){
        return this.queryRepository.buscarTop3(id).stream()
        .map(conta -> Transformer.transform(conta, ContaDTO.class))
        .collect(Collectors.toList());
    }

    public Optional<Conta> buscarPorId(Long id){
        return this.queryRepository.findById(id);
    }

    public Optional<Conta> buscarPorId_cliente(String id){
        return this.queryRepository.findById_cliente(id);
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

    //COMMAND -> QUERY CONTA
    public void salvar(ContaDTO conta){
        queryRepository.save(Transformer.transform(conta, Conta.class));
    }
    public void atualizar(ContaDTO conta){
        queryRepository.save(Transformer.transform(conta, Conta.class));
    }
    public void deletar(ContaDTO conta){
        queryRepository.deleteById(conta.getId());
    }

    //COMMAND -> QUERY MOVIMENTACAO
    public void salvar(MovimentacaoDTO movimentacao){
        movimentacaoRepository.save(Transformer.transform(movimentacao, Movimentacao.class));
    }
    public void atualizar(MovimentacaoDTO movimentacao){
        movimentacaoRepository.save(Transformer.transform(movimentacao, Movimentacao.class));
    }
    public void deletar(MovimentacaoDTO movimentacao){
        movimentacaoRepository.deleteById(movimentacao.getId());
    }
}
