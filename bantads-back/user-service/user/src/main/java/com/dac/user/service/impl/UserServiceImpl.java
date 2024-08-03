package com.dac.user.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Optional <UserModel> findById(String id) {
        Optional<UserModel> user = userRepository.findById(id);
 
        return user;
     }
 
    @Override
    public UserModel create(UserModel user) {
        if(userRepository.existsByCpf(user.getCpf()) || userRepository.existsByEmail(user.getEmail())) return null;
        return userRepository.save(user);

    }

    @Override
    public void atualizar(String id, UserModel user) {
        Optional<UserModel> userBd = userRepository.findById(id);

        if (userBd.isPresent()) {
            UserModel existingUser = userBd.get();

            // Keep id and cpf unchanged
            existingUser.setNome(user.getNome());
            existingUser.setEmail(user.getEmail());
            existingUser.setSalario(user.getSalario());
            existingUser.setTelefone(user.getTelefone());
            existingUser.setEstado(user.getEstado());
            
            existingUser.setTipo(user.getTipo());
            existingUser.setLogradouro(user.getLogradouro());
            existingUser.setNumero(user.getNumero());
            existingUser.setComplemento(user.getComplemento());
            existingUser.setCep(user.getCep());
            existingUser.setCidade(user.getCidade());
            existingUser.setUf(user.getUf());

            userRepository.save(existingUser);
        } else {
            throw new NoSuchElementException();
        }
}


    @Override
    public boolean delete(String id) {
        userRepository.deleteById(id);   
        return true; 
    }

    public Boolean deletarPorId(String id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        
        userRepository.deleteById(id);
        
        return !userRepository.existsById(id);
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

    public ClienteDTO findByIdClienteDTO(ClienteDTO cliente){
        
        Optional<UserModel> buscado = this.userRepository.findById(cliente.getUuid());
        if(!buscado.isPresent())
            return null;

        return Transformer.transform(buscado.get(), ClienteDTO.class);    
    }

    public ClienteDTO atualizarRabbit(ClienteDTO cliente){
        Optional<UserModel> old = this.findById(cliente.getUuid());
        if (!old.isPresent()) {
            return null;
        }
        UserModel atualizada = old.get();
        atualizada.setEmail(cliente.getEmail());
        atualizada.setTelefone(cliente.getTelefone());
        atualizada.setSalario(cliente.getSalario());
        atualizada.setEstado(cliente.getEstado());
        atualizada.setNome(cliente.getNome());
        atualizada.setTipo(cliente.getTipo());
        atualizada.setLogradouro(cliente.getLogradouro());
        atualizada.setNumero(cliente.getNumero());
        atualizada.setComplemento(cliente.getComplemento());
        atualizada.setCep(cliente.getCep());
        atualizada.setCidade(cliente.getCidade());
        atualizada.setUf(cliente.getUf());
    
        ClienteDTO salvo = Transformer.transform(this.userRepository.save(atualizada), ClienteDTO.class);
    
        return salvo;
    }

    public List<ClienteDTO> listarPorEstado(int estado, String id){

        GenericData<String> data = new GenericData<>();
        data.setDto(id);
        //PUXAR CONTAS DE GERENTE COM ESTADO 0
        Message msgConta = new Message<>(UUID.randomUUID().toString(), 
			"requestPending", data, "conta", "cliente.response");    

		List<ContaDTO> contas = producer.sendRequest(msgConta)
            .map(response -> {
                @SuppressWarnings("unchecked")
                GenericData<ContaDTO> dataResponse = Transformer.transform(response, GenericData.class);
				return dataResponse.getList();
            })
            .block();
        //PUXAR CLIENTES QUE CONSTAM NA LISTA
        List<ClienteDTO> clientes = new ArrayList<>();
        for (ContaDTO conta : contas) {
            Optional<UserModel> buscado = userRepository.findById(conta.getId_cliente());
            if (buscado.isPresent()) {
                ClienteDTO cliente = Transformer.transform(buscado.get(), ClienteDTO.class);
                clientes.add(cliente);
            }
        }

        return clientes;

    }
    
    
}
