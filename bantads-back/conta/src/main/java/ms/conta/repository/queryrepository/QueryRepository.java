package ms.conta.repository.queryrepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ms.conta.models.Conta;
import ms.conta.models.aggregation.GerenteContaAggregation;

public interface QueryRepository extends JpaRepository<Conta, Long>{

    @Query(value = "SELECT c.id_gerente AS Id_gerente, COUNT(c.id) AS Account_count" 
    + " FROM conta AS c GROUP BY c.id_gerente", nativeQuery = true)
    List<GerenteContaAggregation> groupByManager();

    @Query(value = "SELECT * FROM conta AS c where id_cliente = :id_cliente", nativeQuery = true)
    Optional<Conta> findById_cliente(String id_cliente);

    @Query(value = "SELECT * FROM conta AS c"
    + " WHERE c.estado = :estado AND c.id_gerente = :idGerente", nativeQuery = true)
    List<Conta> findByEstadoAndGroupByManager(@Param("idGerente") String id, @Param("estado") int estado);

    @Query(value = "SELECT * FROM conta AS c " + 
    "WHERE c.estado != 0 AND c.id_gerente = :idGerente", nativeQuery = true)
    List<Conta> listByGerente(@Param("idGerente") String idGerente);


}
