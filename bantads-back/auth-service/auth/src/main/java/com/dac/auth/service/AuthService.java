package com.dac.auth.service;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dac.auth.dto.AuthDTO;
import com.dac.auth.utils.HashingUtils;
import com.dac.auth.utils.Transformer;
import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;

@Service
public class AuthService {

    @Autowired 
    AuthRepository repo;


    public AuthDTO salvar(AuthModel auth){
        AuthModel salvo = this.repo.save(auth);
        return Transformer.transform(salvo, AuthDTO.class);

    }


    public Boolean deletarPorId(String id) {
        if (!repo.existsById(id)) {
            return false;
        }
        
        repo.deleteById(id);
        
        return !repo.existsById(id);
    }
    
    public Boolean existsByemail(String email){
        if (repo.existsById(email)) {
            return true;
        }

        return false;
    }


    public void atualizar(String email, AuthModel auth) {
        Optional<AuthModel> authBd = repo.findById(email);
    
        
        if (authBd.isPresent()) {
           
            AuthModel existingAuth = authBd.get();
            
            existingAuth.setEmail(auth.getEmail());
            existingAuth.setSenha(HashingUtils.hashPassword(auth.getSenha(), authBd.get().getSalt()));
         
            repo.save(existingAuth);
            
        } else {
            throw new NoSuchElementException();
        }
    }
    
}
