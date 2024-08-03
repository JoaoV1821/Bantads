package com.dac.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dac.user.models.UserModel;
import java.util.List;



@Repository

public interface UserRepository extends JpaRepository<UserModel, String>{
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    UserModel findByEmail(String email);
    List<UserModel> findByEstado(int estado);
}
