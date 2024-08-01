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


    @GetMapping("/{uuid}")
    public ResponseEntity<UserDTO> findById(@PathVariable UUID uuid) {
        Optional<UserModel> user = userService.findByUUID(uuid);

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
            .buildAndExpand(userCreated.getUuid())
            .toUri();
    
            return ResponseEntity.created(location).body(userCreated);

        } else if (!userService.findByEmail(user.getEmail())) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.status(500).build();
    }

    @PutMapping("/atualizar")
    public ResponseEntity<UserModel> atualizar(@PathVariable UUID uuid, @RequestBody UserModel user) {
        Optional<UserModel> bd = userService.findByUUID(uuid);

        if (!bd.isEmpty()) {
            userService.atualizar(uuid, user);
            return ResponseEntity.ok(user);

        } else if (bd.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    
    } 


    @DeleteMapping("/{uuid}")
    public ResponseEntity<UserModel> deletar(@PathVariable UUID uuid) {
        Optional<UserModel> bd = userService.findByUUID(uuid);

        if (!bd.isEmpty()) {
            userService.delete(uuid);
            return ResponseEntity.status(200).build();

        } else if (bd.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(500).build();
    }

}
