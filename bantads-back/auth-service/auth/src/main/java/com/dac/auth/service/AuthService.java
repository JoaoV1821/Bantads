package com.dac.auth.service;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.dac.auth.utils.HashingUtils;
import com.dac.auth.utils.Transformer;
import com.dac.auth.dto.AuthDTO;
import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;

@Service
public class AuthService {

    @Autowired 
    AuthRepository repo;


    public AuthDTO salvar(AuthModel auth){
        
        auth.setUuid(UUID.randomUUID().toString());
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
        if (!repo.findById(email).isEmpty()) {
            return false;
        }
        
        repo.deleteById(email);
        
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
            
            existingAuth.setEmail(auth.getEmail());
            existingAuth.setSenha(HashingUtils.hashPassword(auth.getSenha(), authBd.get().getSalt()));
            existingAuth.setActive(auth.isActive());
    
            repo.save(existingAuth);
            
        } else {
            throw new NoSuchElementException();
        }
    }
    
}
