package ms.conta.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import shared.Message;

@Component
public class Consumer {
    
    @RabbitListener(queues = "conta") 
    public void receiveMessage(Message<?> message){
        System.out.println("Received message on contaApplication" + message);
    }

}
