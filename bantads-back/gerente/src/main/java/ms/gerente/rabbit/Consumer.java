package ms.gerente.rabbit;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ms.gerente.Gerente;
import ms.gerente.GerenteRepository;
import ms.gerente.GerenteService;
import ms.gerente.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.AuthDTO;
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

        try {
            
            switch (message.getRequest()) {
                case "requestManagerForNewAccount":
                    response = handleRequestManagerForNewAccount(message);
                    break;
                case "saveManager":
                    response = handleSaveManager(message);
                    break;
                case "deleteManager":
                    response = handleDeleteManager(message);
                    break;
                default:
                    return;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
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
            @SuppressWarnings("unchecked")
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

    private Message<GerenteDTO> handleSaveManager(Message<?> message) {
        Message<GerenteDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<GerenteDTO> novo = (GenericData<GerenteDTO>) message.getData();
        GerenteDTO salvo = service.salvar(novo.getDto());
        
        if (salvo != null) {
            GenericData<GerenteDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, GerenteDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<GerenteDTO> handleDeleteManager(Message<?> message) {
        Message<GerenteDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<GerenteDTO> gerente = (GenericData<GerenteDTO>) message.getData();

        if (service.deletarPorId(gerente.getDto().getId())) {
            response.setData(gerente);
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
