package com.dac.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;
import com.dac.auth.util.Transformer;

import shared.dtos.AuthDTO;

@Service
public class AuthService {
    
    @Autowired AuthRepository repo;

    public AuthDTO salvar(AuthDTO auth){
        AuthModel salvo = this.repo.save(Transformer.transform(auth, AuthModel.class));
        return Transformer.transform(salvo, AuthDTO.class);
    }

    public Boolean deletarPorId(String id) {
        if (!repo.existsById(id)) {
            return false;
        }
        
        repo.deleteById(id);
        
        return !repo.existsById(id);
    }
}
