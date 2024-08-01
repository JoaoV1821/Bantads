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
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional <UserModel> findById(String id) {
       Optional<UserModel> user = userRepository.findById(id);

       return user;
    }
 
    @Override
    public UserModel create(UserModel user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return null;
        }

        if (userRepository.existsByCpf(user.getEmail())) {
            return null;
        }

        return userRepository.save(user);

    }

    @Override
    public void atualizar(String id, UserModel user) {
        Optional<UserModel> userBd = userRepository.findById(id);
    
        
        if (userBd.isPresent()) {
           
            UserModel existingUser = userBd.get();

            existingUser.setId(existingUser.getId());
            //existingUser.setCpf(existingUser.getCpf());
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
    public void delete(String id) {
        userRepository.deleteById(id);    
    }

    @Override
    public Boolean deletarPorId(String id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        
        userRepository.deleteById(id);
        
        return !userRepository.existsById(id);
    }
    
}
