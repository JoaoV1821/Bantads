package ms.conta.repository.queryrepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ms.conta.models.Conta;
import ms.conta.models.aggregation.GerenteContaAggregation;
import shared.dtos.TelaInicialDTO;

public interface QueryRepository extends JpaRepository<Conta, Long>{

    @Query(value = "SELECT c.id_gerente AS Id_gerente, COUNT(c.id) AS Account_count" 
    + " FROM conta AS c GROUP BY c.id_gerente", nativeQuery = true)
    List<GerenteContaAggregation> groupByManager();

    @Query(value = "SELECT * FROM conta AS c where id_cliente = :id_cliente", nativeQuery = true)
    Optional<Conta> findById_cliente(String id_cliente);

    @Query(value = "SELECT * FROM conta AS c where id_gerente = :id_gerente LIMIT 1", nativeQuery = true)
    //TEM LIMIT 1 PARA ASSOCIAR APENAS UMA CONTA A UM GERENTE NOVO
    Optional<Conta> findById_gerente(String id_gerente);

    @Query(value = "SELECT * FROM conta AS c"
    + " WHERE c.estado = :estado AND c.id_gerente = :idGerente", nativeQuery = true)
    List<Conta> findByEstadoAndGroupByManager(@Param("idGerente") String id, @Param("estado") int estado);

    @Query(value = "SELECT * FROM conta AS c " + 
    "WHERE c.estado != 0 AND c.id_gerente = :idGerente", nativeQuery = true)
    List<Conta> listByGerente(@Param("idGerente") String idGerente);

    @Query(value = "SELECT * FROM conta AS c WHERE c.id_gerente = :idGerente " +
    "ORDER BY c.saldo DESC LIMIT 3", nativeQuery = true)
    List<Conta> buscarTop3(@Param("idGerente") String idGerente);

    @Query("SELECT new shared.dtos.TelaInicialDTO(c.id_gerente, COUNT(c.id), " +
        "SUM(CASE WHEN c.saldo > 0 THEN c.saldo ELSE 0 END), " +
        "SUM(CASE WHEN c.saldo < 0 THEN c.saldo ELSE 0 END)) " +
        "FROM Conta c " +
        "GROUP BY c.id_gerente")
    List<TelaInicialDTO> buscarContasGroupByGerenteSumSaldos();

    @Query(value = "SELECT c.id_gerente " +
                   "FROM conta c " +
                   "GROUP BY c.id_gerente " +
                   "ORDER BY COUNT(c.id) DESC " +
                   "LIMIT 1", nativeQuery = true)
    String buscarGerenteComMaisContas();

}
