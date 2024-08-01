package com.dac.user.service.impl;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dac.user.models.UserModel;
import com.dac.user.repository.UserRepository;
import com.dac.user.service.UserService;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean findByEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            return false;
        };

        return true;
        
    }

    @Override
    public Optional <UserModel> findByUUID(String uuid) {
       Optional<UserModel> user = userRepository.findById(uuid);

       return user;
    }
 
    @Override
    public UserModel create(UserModel user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Já existe uma conta cadastrada neste email");
        }

        return userRepository.save(user);

    }

    @Override
    public void atualizar(String uuid, UserModel user) {
        Optional<UserModel> userBd = userRepository.findById(uuid);
    
        
        if (userBd.isPresent()) {
           
            UserModel existingUser = userBd.get();

            existingUser.setUuid(uuid);
            existingUser.setNome(user.getNome());
            existingUser.setEmail(user.getEmail());
            existingUser.setEndereco(user.getEndereco());
            existingUser.setSalario(user.getSalario());
            existingUser.setTelefone(user.getTelefone());
         
            userRepository.save(existingUser);
            
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void delete(String uuid) {
        userRepository.deleteById(uuid);    
    }
    
}
