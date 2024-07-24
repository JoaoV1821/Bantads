package ms.conta.rabbit;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ms.conta.ContaRepository;

import ms.conta.util.Transformer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ContaDTO;

@Component
public class Consumer {

    @Autowired ContaRepository repo;
    @Autowired RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = "conta") 
    public void receiveMessage(Message<?> message){
        System.out.println("Received message on contaApplication" + message);
    switch (message.getRequest()) {
            case "listAll":

                Message<shared.dtos.ContaDTO> response = new Message<>();

                List<ContaDTO> list = 
                    this.repo.findAll().stream()
                    .map(c -> Transformer.transform(c, ContaDTO.class))
                    .collect(Collectors.toList());
                
                if(list != null){
                    GenericData<ContaDTO> data = new GenericData<>();
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
