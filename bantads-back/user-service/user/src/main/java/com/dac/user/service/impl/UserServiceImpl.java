package com.dac.user.service.impl;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dac.user.models.UserModel;
import com.dac.user.rabbit.Producer;
import com.dac.user.repository.UserRepository;
import com.dac.user.service.UserService;
import com.dac.user.utils.Transformer;

import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;


@Service
public class UserServiceImpl implements UserService {

    @Autowired Producer producer;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional <UserModel>findByEmail(String email) {
        return userRepository.findById(email);
    }

 
    public Optional <UserModel> findByUUID(String uuid) {
       Optional<UserModel> user = userRepository.findById(uuid);

       return user;
    }
 
    @Override
    public UserModel create(UserModel user) {
        
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


    public Pair<ClienteDTO, ContaDTO> clienteComConta(String id){
        if(!userRepository.existsById(id)) return null;

        ClienteDTO cliente = Transformer.transform(findByUUID(id).get(), ClienteDTO.class);

        GenericData<ClienteDTO> data = new GenericData<>();
        data.setDto(cliente);
        Message<ClienteDTO> msgConta = new Message<>(UUID.randomUUID().toString(), 
			"requestAccount", data, "conta", "cliente.response");    

		ContaDTO conta = producer.sendRequest(msgConta)
            .map(response -> {
                @SuppressWarnings("unchecked")
                GenericData<ContaDTO> dataResponse = Transformer.transform(response, GenericData.class);
				return dataResponse.getDto();
            })
            .block();

        if (conta == null) return null;
        
        return new Pair<ClienteDTO, ContaDTO>(cliente, conta);

   
    }
  
  @Override
   public boolean delete(String uuid) {
        userRepository.deleteById(uuid);
        
        return true;

   }
}
