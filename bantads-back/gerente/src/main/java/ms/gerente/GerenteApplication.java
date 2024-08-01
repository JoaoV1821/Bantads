package ms.gerente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GerenteApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(GerenteApplication.class, args);
	}

	@Autowired private GerenteRepository repo;
	@Override
	public void run(String ...args) throws Exception{
		this.repo.save(new Gerente(1L, "000", "gerente@email.com", "9999-8888"));
	}

}
