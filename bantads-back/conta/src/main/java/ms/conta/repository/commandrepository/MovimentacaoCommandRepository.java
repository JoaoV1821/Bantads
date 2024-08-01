package ms.conta.repository.commandrepository;

import org.springframework.data.jpa.repository.JpaRepository;

import ms.conta.models.Movimentacao;

public interface MovimentacaoCommandRepository extends JpaRepository<Movimentacao, Long>{
    
}
