package ms.conta.repository.commandrepository;

import org.springframework.data.jpa.repository.JpaRepository;

import ms.conta.models.Conta;

public interface CommandRepository extends JpaRepository<Conta, String>{
    
}
