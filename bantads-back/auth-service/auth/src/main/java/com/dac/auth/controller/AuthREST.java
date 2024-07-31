package com.dac.auth.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dac.auth.model.AuthModel;
import com.dac.auth.model.LoginModel;
import com.dac.auth.repository.AuthRepository;
import com.dac.auth.service.AuthService;
import com.dac.auth.dto.AuthDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import com.dac.auth.utils.HashingUtils;
import com.dac.auth.utils.SaltGenerator;
import com.dac.auth.utils.Transformer;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin
@RestController
public class AuthREST {

    @Autowired
    AuthRepository authRepository;

    @Autowired
    AuthService authService;


    @PostMapping("/auth")
    ResponseEntity<AuthDTO> auth(@RequestBody LoginModel login) {

       Optional<AuthModel> authBd = authRepository.findById(login.getEmail());

       if (!authBd.isEmpty()) {
            String hashPassword = HashingUtils.hashPassword(login.getSenha(), authBd.get().getSalt());

            if (hashPassword.equals(authBd.get().getSenha())) {
                return ResponseEntity.ok().body(Transformer.transform(authBd, AuthDTO.class));

            } else {
                return ResponseEntity.status(401).build();
            }

       } else if (authBd.isEmpty()) {
            return ResponseEntity.status(404).build();
       }

       return ResponseEntity.status(500).build();

        // TODO: enviar email
    }


    @PostMapping("/registrar")
    public ResponseEntity<Object> register(@RequestBody AuthModel login) {

        if (!authService.existsByemail(login.getEmail())) {

            String salt = SaltGenerator.generateSalt();
            String hashPassword = HashingUtils.hashPassword(login.getSenha(), salt);

            login.setSenha(hashPassword);
            login.setSalt(salt);

            authService.salvar(login);
            return  ResponseEntity.status(201).build();

        } else if (authService.existsByemail(login.getEmail())) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.status(500).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> delete(@RequestBody String email) {

        if (authService.deletarPorId(email)) {
            return  ResponseEntity.status(200).build();

        } else if (!authService.deletarPorId(email)) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    }

    @PutMapping("/atualizar")
    public ResponseEntity<Object> atualizar(@PathVariable String email, @RequestBody AuthModel login) {
       Optional<AuthModel> authbd =  authRepository.findById(email);

       if (!authbd.isEmpty()) {
            authService.atualizar(email, login);

            return ResponseEntity.status(200).build();

       } else if (authbd.isEmpty()) {
         return ResponseEntity.status(404).build();

       }

        return ResponseEntity.status(500).build();
    }
    
    @GetMapping("/users")
    public List<AuthModel> users() {
        return authRepository.findAll();
    }

}
