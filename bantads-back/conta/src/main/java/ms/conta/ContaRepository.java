package ms.conta;

import org.springframework.data.jpa.repository.JpaRepository;

import ms.conta.models.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long>{
   
}
