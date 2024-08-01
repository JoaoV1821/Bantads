package ms.conta;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ms.conta.models.Movimentacao;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long>{
    @Query(nativeQuery = true, value = "SELECT * FROM movimentacao WHERE :id = id AND :dataIni < data")
    List<Movimentacao> findByDataInicial(Long id, Date dataIni);

    @Query(nativeQuery = true, 
        value = "SELECT * FROM movimentacao WHERE :id = id AND data > :dataIni AND data < :dataFim")
    List<Movimentacao> findByDataInicialEDataFinal(Long id, Date dataIni, Date dataFim);
}
