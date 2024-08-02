package com.dac.user.controller;

import java.net.URI;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.dac.user.dto.UserDTO;
import com.dac.user.models.UserModel;
import com.dac.user.service.UserService;
import com.dac.user.service.impl.UserServiceImpl;
import com.dac.user.utils.Transformer;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;




@CrossOrigin
@RestController
@RequestMapping("/cliente")
public class UserController {

    @Autowired UserServiceImpl service;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/find/{uuid}")
    public ResponseEntity<UserDTO> findById(@PathVariable String uuid) {
        Optional<UserModel> user = userService.findByUUID(uuid);

        if (!user.isEmpty()) {
            UserDTO userDTO = Transformer.transform(user, UserDTO.class);

            return ResponseEntity.ok(userDTO);

        } else if (user.isEmpty()) {
            return ResponseEntity.status(404).build();

        }

        return ResponseEntity.status(500).build();
    }


    @PostMapping("/registrar")
    public ResponseEntity<UserModel> create(@RequestBody UserModel user) {

        if (userService.findByEmail(user.getEmail())) {
            user.setUuid(UUID.randomUUID().toString());
            UserModel userCreated = userService.create(user);

            user.setUuid(UUID.randomUUID().toString());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{uuid}")
            .buildAndExpand(userCreated.getUuid())
            .toUri();
    
            return ResponseEntity.created(location).body(userCreated);

        } else if (!userService.findByEmail(user.getEmail())) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.status(500).build();
    }


    @PutMapping("/update/{uuid}")
    public ResponseEntity<UserModel> atualizar(@PathVariable String uuid, @RequestBody UserModel user) {
        Optional<UserModel> bd = userService.findByUUID(uuid);

        if (!bd.isEmpty()) {
            userService.atualizar(uuid, user);
            return ResponseEntity.ok(user);

        } else if (bd.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    
    } 

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<UserModel> deletar(@PathVariable String uuid) {
        Optional<UserModel> bd = userService.findByUUID(uuid);

        if (!bd.isEmpty()) {
            userService.delete(uuid);
            return ResponseEntity.status(200).build();

        } else if (bd.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    }

    @GetMapping("/tela-inicial/{id}")
    public ResponseEntity<Pair<ClienteDTO, ContaDTO>> telaInicial(@PathVariable String id) {
        Pair<ClienteDTO, ContaDTO> pair = this.service.clienteComConta(id);
        
        if(pair != null){
            return ResponseEntity.ok(pair);
        }

        return pair != null ? ResponseEntity.ok(pair) : ResponseEntity.notFound().build();
    }
    

}
