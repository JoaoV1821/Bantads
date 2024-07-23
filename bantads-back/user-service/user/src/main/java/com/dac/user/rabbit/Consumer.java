package com.dac.user.rabbit;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dac.user.repository.UserRepository;
import com.dac.user.util.Transformer;

import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;

@Component
public class Consumer {
    
    @Autowired Producer producer;
    @Autowired UserRepository repo;
    @Autowired RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "cliente") 
    public void receiveMessage(Message<?> message){
        System.out.println("Received message on clienteApplication" + message);
        switch (message.getRequest()) {
            case "listAll":

                Message<ClienteDTO> response = new Message<>();

                List<ClienteDTO> list = 
                    this.repo.findAll().stream()
                    .map(c -> Transformer.transform(c, ClienteDTO.class))
                    .collect(Collectors.toList());
                
                if(list != null){
                    GenericData<ClienteDTO> data = new GenericData<>();
                    data.setList(list);
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
        
            default:
                break;
        }
    }

}
