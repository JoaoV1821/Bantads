package ms.conta;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ms.conta.models.Conta;
import ms.conta.models.GerenteContaAggregation;

public interface ContaRepository extends JpaRepository<Conta, String>{

    @Query(value = "SELECT c.id_gerente AS Id_gerente, COUNT(c.id) AS Account_count" 
    + " FROM conta AS c GROUP BY c.id_gerente", nativeQuery = true)
    List<GerenteContaAggregation> groupByManager();

    @Query(value = "SELECT * FROM conta AS c where id_cliente = :id_cliente", nativeQuery = true)
    Optional<Conta> findById_cliente(String id_cliente);

}
