package com.dac.user.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.Optional;
import com.dac.user.dto.UserDTO;
import com.dac.user.models.UserModel;
import com.dac.user.service.UserService;
import com.dac.user.utils.Transformer;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin
@RestController
@RequestMapping("/cliente")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable String id) {
        Optional<UserModel> user = userService.findById(id);

        if (!user.isEmpty()) {
            UserDTO userDTO = Transformer.transform(user, UserDTO.class);

            return ResponseEntity.ok(userDTO);

        } else if (user.isEmpty()) {
            return ResponseEntity.status(404).build();

        }

        return ResponseEntity.status(500).build();
    }


    @PostMapping
    public ResponseEntity<UserModel> create(@RequestBody UserModel user) {

        if (userService.findByEmail(user.getEmail())) {
            UserModel userCreated = userService.create(user);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{uuid}")
            .buildAndExpand(userCreated.getId())
            .toUri();
    
            return ResponseEntity.created(location).body(userCreated);

        } else if (!userService.findByEmail(user.getEmail())) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.status(500).build();
    }

    @PutMapping("/atualizar")
    public ResponseEntity<UserModel> atualizar(@PathVariable String id, @RequestBody UserModel user) {
        Optional<UserModel> bd = userService.findById(id);

        if (!bd.isEmpty()) {
            userService.atualizar(id, user);
            return ResponseEntity.ok(user);

        } else if (bd.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    
    } 


    @DeleteMapping("/{id}")
    public ResponseEntity<UserModel> deletar(@PathVariable String id) {
        Optional<UserModel> bd = userService.findById(id);

        if (!bd.isEmpty()) {
            userService.delete(id);
            return ResponseEntity.status(200).build();

        } else if (bd.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    }

}
