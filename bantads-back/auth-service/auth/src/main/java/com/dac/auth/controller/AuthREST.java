package com.dac.auth.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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

import com.dac.auth.utils.EmailUtils;
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

    @PostMapping("/auth/autenticar")
    ResponseEntity<AuthDTO> auth(@RequestBody LoginModel login) {
        
       Optional<AuthModel> authBd = authRepository.findByEmail(login.getEmail());

       if (!authBd.isEmpty()) {
            String hashPassword = HashingUtils.hashPassword(login.getSenha(), authBd.get().getSalt());

            System.out.println(hashPassword.equals(authBd.get().getSenha()));
            System.out.println(hashPassword);
            System.out.println(authBd.get().getSenha());
            System.out.println(authBd.get().toString());

                if (hashPassword.equals(authBd.get().getSenha()) ) {
                    return ResponseEntity.ok().body(Transformer.transform(authBd, AuthDTO.class));
    
                } else {
                
                    return ResponseEntity.status(401).build();
                    
                }
                   
           
       } else if (authBd.isEmpty()) {
            return ResponseEntity.status(401).build();
       }

       return ResponseEntity.status(500).build();
    }


    @PostMapping("/auth/registrar")
    public ResponseEntity<Object> register(@RequestBody AuthModel login ) throws AddressException, MessagingException {

        Optional <AuthModel> authBd = authRepository.findByEmail(login.getEmail());

        if (authBd.isEmpty()) {

            login.setActive(false);
            login.setUuid(UUID.randomUUID().toString());
            login.setSalt("");
            login.setSenha("");

            authService.salvar(login);

            return  ResponseEntity.ok().body(login);

        } else if (authBd.isEmpty() == false) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.status(500).build();
    }


    @DeleteMapping("/auth/delete/{email}")
    public ResponseEntity<Object> delete(@PathVariable String email) {

        if (authService.deletarPorEmail(email)) {
            return  ResponseEntity.status(200).build();

        } else if (!authService.deletarPorEmail(email)) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    }


    @PutMapping("/auth/update/{email}")
    public ResponseEntity<Object> atualizar(@PathVariable String email, @RequestBody AuthModel login) {
       Optional<AuthModel> authbd = authRepository.findByEmail(email);

       if (!authbd.isEmpty()) {

            String hashPassword = HashingUtils.hashPassword(login.getSenha(), authbd.get().getSalt());
            login.setSenha(hashPassword);
            authService.atualizar(email, login);

            return ResponseEntity.status(200).build();

       } else if (authbd.isEmpty()) {

         return ResponseEntity.status(404).build();

       }
        return ResponseEntity.status(500).build();
    }
    
    @GetMapping("/auth/users")
    public List<AuthModel> users() {
        return authRepository.findAll();
    }


    @PutMapping("/auth/aprovar/{email}")
    public ResponseEntity<Object> aprovar(@PathVariable String email) throws AddressException, MessagingException {

       Optional <AuthModel> authBd = authRepository.findByEmail(email);

        if (!authBd.isEmpty()) {

            String password = HashingUtils.gerarSenha(6);
            String salt = SaltGenerator.generateSalt();
            String hashPassword = HashingUtils.hashPassword(password, salt);
            String msg = "Sua senha: " + "".concat(password);

            authBd.get().setSenha(hashPassword);
            authBd.get().setSalt(salt);
            authBd.get().setActive(true);
    
            authService.atualizar(email, authBd.get());
    
            EmailUtils.enviarEmail(msg, "Nova conta", authBd.get().getEmail());
            
            return ResponseEntity.ok().body(authBd.get());

        } else if (authBd.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
       
    }
}
