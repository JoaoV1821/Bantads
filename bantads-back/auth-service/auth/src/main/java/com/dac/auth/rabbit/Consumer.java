package com.dac.auth.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;
import com.dac.auth.service.AuthService;
import com.dac.auth.utils.Transformer;

import shared.GenericData;
import shared.Message;
import shared.dtos.AuthDTO;

@Component
public class Consumer {

    @Autowired Producer producer;
    @Autowired AuthRepository repo;
    @Autowired AuthService service;
    @Autowired RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "auth") 
    public void receiveMessage(Message<?> message) {
        System.out.println("Received message on authApplication" + message);

        Message<?> response = null;

        switch (message.getRequest()) {
            case "saveAuth":
                response = handleSaveAuth(message);
                break;
            case "deleteAuth":
                response = handleDeleteAuth(message);
                break;
            default:
                return;
        }

        sendResponse(message, response);
    }

    private Message<AuthDTO> handleSaveAuth(Message<?> message) {
        Message<AuthDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<AuthDTO> novo = (GenericData<AuthDTO>) message.getData();
        com.dac.auth.dto.AuthDTO salvoDTO = service.salvar(Transformer.transform(novo.getDto(),AuthModel.class));
        AuthDTO salvo = Transformer.transform(salvoDTO, AuthDTO.class);

        if (salvo != null) {
            GenericData<AuthDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, AuthDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<AuthDTO> handleDeleteAuth(Message<?> message) {
        Message<AuthDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<AuthDTO> auth = (GenericData<AuthDTO>) message.getData();

        if (service.deletarPorEmail(auth.getDto().getEmail())) {
            response.setData(auth);
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
