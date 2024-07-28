package ms.conta.rabbit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ms.conta.ContaRepository;
import ms.conta.ContaService;
import ms.conta.models.GerenteContaAggregation;
import ms.conta.util.Transformer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;

@Component
public class Consumer {

    @Autowired ContaRepository repo;
    @Autowired ContaService service;
    @Autowired RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = "conta") 
    public void receiveMessage(Message<?> message){
        System.out.println("Received message on contaApplication" + message);
        switch (message.getRequest()) {
            case "listAll": {
                Message<ContaDTO> response = new Message<>();
    
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
            }
                break;
        
            case "updateAccount":{
                
                Message<ContaDTO> response = new Message<>();

                //GenericData<ClienteDTO> novo = Transformer.transform(message.getData(), GenericData.class);
                GenericData novo = message.getData();
                ContaDTO salvo = this.service.atualizarPorId_cliente(Transformer.transform(novo.getDto(), ContaDTO.class));
                
                if(salvo != null){
                    GenericData<ContaDTO> data = new GenericData<>();
                    data.setDto(Transformer.transform(salvo, ContaDTO.class));
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
            case "saveAccount":{
                Message<ContaDTO> response = new Message<>();

                //GenericData<ContaDTO> novo = Transformer.transform(message.getData(), GenericData.class);
                GenericData novo = message.getData();
                ContaDTO salvo = this.service.salvar(Transformer.transform(novo.getDto(), ContaDTO.class));
                
                if(salvo != null){
                    GenericData<ContaDTO> data = new GenericData<>();
                    data.setDto(Transformer.transform(salvo, ContaDTO.class));
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
            case "deleteAccount":{

                Message<ContaDTO> response = new Message<>();

                //GenericData<ClienteDTO> novo = Transformer.transform(message.getData(), GenericData.class);
                GenericData<ContaDTO> cliente = (GenericData<ContaDTO>) message.getData();
                
                if(this.service.deletarPorId(cliente.getDto().getId())){
                    //seta a conta como data para saber na saga se foi bem sucedido
                    response.setData(cliente);
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
                
            case "requestManagerWithLeastAccounts":{
                //esse case recebe uma lista de String de id de gerentes
                GenericData lista = message.getData();
                List<String> lista_id_gerente = lista.getList();

                //conta faz um map de contas por gerente(group)
                Map<String, Integer> mapManagerAgg = this.repo.groupByManager().stream()
                    .collect(Collectors.toMap(
                        GerenteContaAggregation::getId_gerente,
                        GerenteContaAggregation::getAccount_count
                    ));
    
                //mescla com a lista de gerentes e retorna o id com menos contas
                Map<String, Integer> resultMerge = lista_id_gerente.stream()
                    .collect(Collectors.toMap(
                        id -> id,
                        id -> mapManagerAgg.getOrDefault(id, 0)
                    ));

                Message<String> response = new Message<>();
                GenericData<String> data = new GenericData<>();

                Optional<Map.Entry<String, Integer>> managerWithLeastAccounts = resultMerge.entrySet().stream().min(Map.Entry.comparingByValue());
                if(managerWithLeastAccounts.isPresent()){
                    data.setDto(managerWithLeastAccounts.get().getKey());
                    response.setData(data);
                }
                else
                {
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
