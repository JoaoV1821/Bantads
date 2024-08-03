package ms.gerente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GerenteRepository extends JpaRepository<Gerente, String>{

    Boolean existsByEmail(String email);
    Boolean existsByCpf(String cpf);

}
