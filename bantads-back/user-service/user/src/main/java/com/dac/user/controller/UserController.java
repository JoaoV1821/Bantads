package com.dac.user.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import com.dac.user.dto.UserDTO;
import com.dac.user.mapper.UserMapper;
import com.dac.user.models.UserModel;
import com.dac.user.service.UserService;
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


    @GetMapping("/{cpf}")
    public ResponseEntity<UserDTO> findByCpf(@PathVariable String cpf) {
        var user = userService.findByEmail(cpf);
        UserDTO userDTO = UserMapper.toDto(user);

        return ResponseEntity.ok(userDTO);
    }


    @PostMapping
    public ResponseEntity<UserModel> create(@RequestBody UserModel user) {
        var userCreated = userService.create(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{cpf}")
        .buildAndExpand(userCreated.getCpf())
        .toUri();

        return ResponseEntity.created(location).body(userCreated);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<UserModel> atualizar(@PathVariable String cpf, @RequestBody UserModel user) {
        userService.atualizar(cpf, user);
        return ResponseEntity.ok(user);
    } 

}
