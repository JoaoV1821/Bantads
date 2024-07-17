package ms.gerente;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.gerente.util.Transformer;

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

    public Optional<Gerente> buscarPorId(Long id){
        return this.gerenteRepository.findById(id);
    }

    public GerenteDTO salvar(GerenteDTO dto) {
        //TODO RELACIONAR CONTAS

        Gerente gerente = Transformer.transform(dto, Gerente.class);
        Gerente savedGerente = this.gerenteRepository.save(gerente);
        return Transformer.transform(savedGerente, GerenteDTO.class);
    }

    public GerenteDTO atualizar(Long id, GerenteDTO dto) {
        Gerente oldGerente = this.buscarPorId(id).orElseThrow(NoSuchElementException::new);
        oldGerente.setEmail(dto.getEmail());
        oldGerente.setTelefone(dto.getTelefone());
        return Transformer.transform(this.gerenteRepository.save(oldGerente), GerenteDTO.class);
    }

    public void remover(Long id){
        //TODO RELACIONAR CONTAS
        
        if(this.listar().size() <= 1){
            throw new IllegalArgumentException("Não é permitido remover o último gerente");
        }
        this.gerenteRepository.deleteById(id);
    }
    
}