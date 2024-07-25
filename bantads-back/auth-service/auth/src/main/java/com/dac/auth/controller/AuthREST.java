package com.dac.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;

import shared.dtos.AuthDTO;

import com.dac.auth.mapper.AuthMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin
@RestController
public class AuthREST {

    @Autowired
    AuthRepository authRepository;

    @PostMapping("/auth")
    ResponseEntity<AuthDTO> auth(@RequestBody AuthModel login) {

        // Verificar no Mongo
        List<AuthModel> cursor = authRepository.findAll();

        for (AuthModel i : cursor) {

            if (login.getEmail().equals(i.getEmail()) && login.getSenha().equals(i.getSenha())) {
                AuthDTO user = AuthMapper.toDto(i);
                return ResponseEntity.ok().body(user);
            }
        }

        return ResponseEntity.status(401).build(); 
    }

    @GetMapping("/users")
    public List<AuthModel> users() {
        return authRepository.findAll();
    }

}
