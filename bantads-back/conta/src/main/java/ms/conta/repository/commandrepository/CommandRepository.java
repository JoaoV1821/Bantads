package ms.conta.repository.commandrepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ms.conta.models.Conta;

public interface CommandRepository extends JpaRepository<Conta, Long>{
    
    /* 
    @Query("UPDATE conta AS c SET c.id_gerente = :newIdGerente "+ 
        " WHERE c.id_gerente = :oldIdGerente")
    void updateGerenteId(@Param("newIdGerente") String newIdGerente, @Param("oldIdGerente") String oldIdGerente);
*/

    @Query(value = "SELECT * FROM conta AS c " + "WHERE c.id_gerente = :id_gerente", nativeQuery = true)
    List<Conta> findAllByIdgerente(String id_gerente);
}
