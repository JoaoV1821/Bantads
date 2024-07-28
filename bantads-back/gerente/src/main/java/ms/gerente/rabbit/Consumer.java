package ms.gerente.rabbit;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ms.gerente.GerenteRepository;
import ms.gerente.GerenteService;
import ms.gerente.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.GerenteDTO;

@Component
public class Consumer {
    
    @Autowired GerenteRepository repo;
    @Autowired GerenteService service;
    @Autowired Producer producer;
    @Autowired RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = "gerente") 
    public void receiveMessage(Message<?> message){

        System.out.println("Received message on gerenteApplication" + message);
        switch (message.getRequest()) {
            case "requestManagerForNewAccount":{
                
                final String[] managerWithLeastAccounts_id = new String[1];

                List<String> list_id_gerentes = this.repo.findAll().stream()
                .map(g -> { return g.getId(); })
                .collect(Collectors.toList());

                GenericData<String> dataList = new GenericData<>();
                dataList.setList(list_id_gerentes);

                Message request = new Message<>(UUID.randomUUID().toString(),
            "requestManagerWithLeastAccounts", dataList , "conta", "gerente.response");

                Mono<GenericData<?>> contaResponse = producer.sendRequest(request);

                contaResponse.map(response -> {
                    GenericData<String> gerente_id = Transformer.transform(response, GenericData.class);
                    System.out.println("gerente " + gerente_id);
                    managerWithLeastAccounts_id[0] = gerente_id.getDto();
                    return gerente_id;
                }).block();

                //puxa gerente por id retornado de conta, responde para saga 
                Message<GerenteDTO> response = new Message<>();
                GerenteDTO salvo = this.service.buscarPorId(managerWithLeastAccounts_id[0]);

                if(salvo != null){
                    GenericData<GerenteDTO> data = new GenericData<>();
                    data.setDto(salvo);
                    response.setData(data);
                }
                else{
                    response.setData(null);
                    response.setRequest("error");
                }

                response.setId(message.getId());
                response.setTarget(message.getReplyTo());
                System.out.println("responseMsg " + response);
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
