package com.dac.user.rabbit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dac.user.models.UserModel;
import com.dac.user.repository.UserRepository;
import com.dac.user.service.UserService;
import com.dac.user.service.impl.UserServiceImpl;
import com.dac.user.utils.Transformer;

import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;

@Component
public class Consumer {

    @Autowired 
    Producer producer;
    
    @Autowired 
    UserRepository repo;
    
    @Autowired 
    UserService service;

    @Autowired 
    UserServiceImpl serviceImpl;
    
    @Autowired 
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "cliente") 
    public void receiveMessage(Message<?> message) {
        System.out.println("Received message on clienteApplication" + message);

        Message<?> response = null;

        switch (message.getRequest()) {
            case "listAll":
                response = handleListAll(message);
                break;
            case "saveClient":
                response = handleSaveClient(message);
                break;
            case "updateClient":
                response = handleUpdateClient(message);
                break;
            case "deleteClient":
                response = handleDeleteClient(message);
                break;
            case "requestClient":
                response = handleRequestClient(message);
                break;
            default:
                return;
        }

        sendResponse(message, response);
    }

    private Message<ClienteDTO> handleListAll(Message<?> message) {
        Message<ClienteDTO> response = new Message<>();

        List<ClienteDTO> list = repo.findAll().stream()
            .map(c -> Transformer.transform(c, ClienteDTO.class))
            .collect(Collectors.toList());

        if (list != null) {
            GenericData<ClienteDTO> data = new GenericData<>();
            data.setList(list);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }

        return response;
    }

    private Message<ClienteDTO> handleSaveClient(Message<?> message) {
        Message<ClienteDTO> response = new Message<>();

        @SuppressWarnings("rawtypes")
        GenericData novo = message.getData();
        UserModel salvo = service.create(Transformer.transform(novo.getDto(), UserModel.class));

        if (salvo != null) {
            GenericData<ClienteDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, ClienteDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }

        return response;
    }

    private Message<ClienteDTO> handleUpdateClient(Message<?> message) {
        Message<ClienteDTO> response = new Message<>();
        GenericData<ClienteDTO> novo = (GenericData<ClienteDTO>) message.getData();

        ClienteDTO salvo = serviceImpl.atualizarRabbit(novo.getDto());

        if (salvo != null) {
            GenericData<ClienteDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, ClienteDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<ClienteDTO> handleDeleteClient(Message<?> message) {
        Message<ClienteDTO> response = new Message<>();

        @SuppressWarnings("unchecked")
        GenericData<ClienteDTO> cliente = (GenericData<ClienteDTO>) message.getData();

        if (serviceImpl.deletarPorId(cliente.getDto().getId())) {
            response.setData(cliente);
        } else {
            response.setData(null);
            response.setRequest("error");
        }

        return response;
    }

    private Message<ClienteDTO> handleRequestClient(Message<?> message) {
        Message<ClienteDTO> response = new Message<>();
        GenericData<ClienteDTO> cliente = (GenericData<ClienteDTO>) message.getData();

        ClienteDTO buscado = serviceImpl.findByIdClienteDTO(cliente.getDto());

        if(buscado != null){
            GenericData<ClienteDTO> data = new GenericData<>();
            data.setDto(buscado);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }

        return response;
    }

    private void sendResponse(Message<?> request, Message<?> response) {
        response.setId(request.getId());
        response.setTarget(request.getReplyTo());

        rabbitTemplate.convertAndSend(response.getTarget(), response, msg -> {
            msg.getMessageProperties().setCorrelationId(response.getId());
            return msg;
        });
    }
}
