package ms.gerente;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.gerente.util.Transformer;
import shared.dtos.GerenteDTO;

@Service
public class GerenteService {
    
    @Autowired
    private GerenteRepository gerenteRepository;

    //TODO 
    //INSERÇÂO DE GERENTE : RELACIONAR CONTAS
    //REMOÇÃO DE GERENTE : RELACIONAR CONTAS
    //RELATÓRIO DE CLIENTES : listar clientes com nome, cpf, limite, saldo, limite (MS cliente + MS conta)
    //DASHBOARD : listar gerentes com n clientes, soma de saldo positivo e soma de saldo negativo (MS conta + MS cliente)

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
    
}