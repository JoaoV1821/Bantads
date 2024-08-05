package com.dac.auth.service;
import java.util.NoSuchElementException;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dac.auth.utils.Transformer;
import com.dac.auth.dto.AuthDTO;
import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;

@Service
public class AuthService {

    @Autowired 
    AuthRepository repo;


    public AuthDTO salvar(AuthModel auth){
        
        //VEM DA SAGA
        //auth.setUuid(UUID.randomUUID().toString());
        System.out.println(repo.existsByEmail(auth.getEmail()));
        if(repo.existsByEmail(auth.getEmail())) return null;
        
        AuthModel salvo = this.repo.save(auth);

        return Transformer.transform(salvo, AuthDTO.class);

    }

    public Boolean deletarPorId(String email) {
        if (!repo.findById(email).isEmpty()) {
            return false;
        }
        
        repo.deleteById(email);
        
        return !repo.existsByEmail(email);
    }

    public Boolean deletarPorEmail(String email) {
        if (!repo.existsByEmail(email)) {
            return false;
        }
        
        repo.deleteByEmail(email);
        
        return !repo.existsByEmail(email);
    }
    
    public Boolean existsByemail(String email){
        if (repo.existsById(email)) {
            return true;
        }

        return false;
    }


    public void atualizar(String email, AuthModel auth) {
        Optional<AuthModel> authBd = repo.findByEmail(email);
    
        if (authBd.isPresent()) {
           
            AuthModel existingAuth = authBd.get();
            
            existingAuth.setUuid(existingAuth.getUuid());
            existingAuth.setEmail(auth.getEmail());
            existingAuth.setSenha(auth.getSenha());
            existingAuth.setActive(auth.isActive());
            existingAuth.setSalt(auth.getSalt());
           
            repo.save(existingAuth);
            
        } else {
            throw new NoSuchElementException();
        }
    }
    
}
