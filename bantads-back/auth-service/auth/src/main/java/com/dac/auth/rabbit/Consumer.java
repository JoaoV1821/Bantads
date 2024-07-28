package com.dac.auth.rabbit;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.internal.bytebuddy.description.type.TypeDescription.Generic;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dac.auth.model.AuthModel;
import com.dac.auth.repository.AuthRepository;
import com.dac.auth.service.AuthService;
import com.dac.auth.util.Transformer;

import shared.GenericData;
import shared.Message;
import shared.dtos.AuthDTO;
import shared.dtos.ClienteDTO;

@Component
public class Consumer {
    
    @Autowired Producer producer;
    @Autowired AuthRepository repo;
    @Autowired AuthService service;
    @Autowired RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "auth") 
    public void receiveMessage(Message<?> message){
        System.out.println("Received message on authApplication" + message);
        switch (message.getRequest()) {
            case "saveAuth":{

                Message<AuthDTO> response = new Message<>();

                //GenericData<AuthDTO> novo = Transformer.transform(message.getData(), GenericData.class);
                GenericData<AuthDTO> novo = (GenericData<AuthDTO>) message.getData();
                AuthDTO salvo = this.service.salvar(novo.getDto());
                
                if(salvo != null){
                    GenericData<AuthDTO> data = new GenericData<>();
                    data.setDto(Transformer.transform(salvo, AuthDTO.class));
                    response.setData(data);
                }
                else{
                    response.setData(null);
                    response.setRequest("error");
                }

                response.setId(message.getId());
                response.setTarget(message.getReplyTo());

                
                rabbitTemplate.convertAndSend(response.getTarget(), response, msg -> {
                    msg.getMessageProperties().setCorrelationId(response.getId());
                    return msg;
                }); 
                break;
            }
            
            case "deleteAuth":{

                Message<AuthDTO> response = new Message<>();

                //GenericData<AuthDTO> novo = Transformer.transform(message.getData(), GenericData.class);
                GenericData<AuthDTO> auth = (GenericData<AuthDTO>) message.getData();
                
                if(this.service.deletarPorId(auth.getDto().getId())){
                    //seta o auth como data para saber na saga se foi bem sucedido
                    response.setData(auth);
                }
                else{
                    response.setData(null);
                    response.setRequest("error");
                }

                response.setId(message.getId());
                response.setTarget(message.getReplyTo());

                
                rabbitTemplate.convertAndSend(response.getTarget(), response, msg -> {
                    msg.getMessageProperties().setCorrelationId(response.getId());
                    return msg;
                }); 
                break;
            }    
            default:
                break;
        }
    }

}
