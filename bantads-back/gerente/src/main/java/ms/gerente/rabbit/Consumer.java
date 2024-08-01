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

    @Autowired 
    GerenteRepository repo;
    
    @Autowired 
    GerenteService service;
    
    @Autowired 
    Producer producer;
    
    @Autowired 
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "gerente") 
    public void receiveMessage(Message<?> message) {
        System.out.println("Received message on gerenteApplication" + message);

        Message<?> response = null;

        switch (message.getRequest()) {
            case "requestManagerForNewAccount":
                response = handleRequestManagerForNewAccount(message);
                break;
            default:
                return;
        }

        sendResponse(message, response);
    }

    private Message<GerenteDTO> handleRequestManagerForNewAccount(Message<?> message) {
        final String[] managerWithLeastAccountsId = new String[1];

        List<String> listIdGerentes = repo.findAll().stream()
            .map(g -> g.getId())
            .collect(Collectors.toList());

        GenericData<String> dataList = new GenericData<>();
        dataList.setList(listIdGerentes);

        Message<?> request = new Message<>(UUID.randomUUID().toString(),
            "requestManagerWithLeastAccounts", dataList, "conta", "gerente.response");

        Mono<GenericData<?>> contaResponse = producer.sendRequest(request);

        contaResponse.map(response -> {
            GenericData<String> gerenteId = Transformer.transform(response, GenericData.class);
            System.out.println("gerente " + gerenteId);
            managerWithLeastAccountsId[0] = gerenteId.getDto();
            return gerenteId;
        }).block();

        Message<GerenteDTO> response = new Message<>();
        GerenteDTO salvo = service.buscarPorId(managerWithLeastAccountsId[0]);

        if (salvo != null) {
            GenericData<GerenteDTO> data = new GenericData<>();
            data.setDto(salvo);
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
