package ms.conta.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.conta.models.Conta;
import ms.conta.models.Movimentacao;
import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.repository.ContaRepository;
import ms.conta.repository.MovimentacaoRepository;
import ms.conta.util.Transformer;
import shared.dtos.ContaDTO;

@Service
public class QueryService {
    
    @Autowired private ContaRepository contaRepository;
    @Autowired private MovimentacaoRepository movimentacaoRepository;

    public List<ContaDTO> listar(){
        return this.contaRepository.findAll().stream()
        .map(conta -> Transformer.transform(conta, ContaDTO.class))
        .collect(Collectors.toList());
    }

    public Optional<Conta> buscarPorId(String id){
        return this.contaRepository.findById(id);
    }

    public Optional<Conta> buscarPorId_cliente(String id){
        return this.contaRepository.findById_cliente(id);
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

    //CUD UPDATE CONTA
    public void salvar(ContaDTO conta){
        contaRepository.save(Transformer.transform(conta, Conta.class));
    }
    public void atualizar(ContaDTO conta){
        contaRepository.save(Transformer.transform(conta, Conta.class));
    }
    public void deletar(ContaDTO conta){
        contaRepository.deleteById(conta.getId());
    }

    //CUD UPDATE MOVIMENTACAO
    public void salvar(MovimentacaoDTO movimentacao){
        contaRepository.save(Transformer.transform(movimentacao, Conta.class));
    }
    public void atualizar(MovimentacaoDTO movimentacao){
        contaRepository.save(Transformer.transform(movimentacao, Conta.class));
    }
    public void deletar(MovimentacaoDTO movimentacao){
        contaRepository.deleteById(movimentacao.getId());
    }
}
