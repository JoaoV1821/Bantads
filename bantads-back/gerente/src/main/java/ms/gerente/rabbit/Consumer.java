package ms.gerente.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import shared.Message;

@Component
public class Consumer {
    
    @RabbitListener(queues = "gerente") 
    public void receiveMessage(Message<?> message){
        System.out.println("Received message on gerenteApplication" + message);
    }

}
