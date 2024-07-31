package com.dac.user.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import com.dac.user.models.UserModel;


@Repository
public interface UserRepository extends CrudRepository< UserModel, UUID>{
    boolean existsByEmail(String email);
}
