package ms.conta.rabbit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ms.conta.models.Conta;
import ms.conta.models.aggregation.GerenteContaAggregation;
import ms.conta.repository.queryrepository.QueryRepository;
import ms.conta.service.CommandService;
import ms.conta.service.QueryService;
import ms.conta.util.Transformer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;
import shared.dtos.TelaInicialDTO;

@Component
public class Consumer {

    @Autowired QueryRepository repo;
    @Autowired CommandService commandService;
    @Autowired QueryService queryService;
    @Autowired RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = "conta") 
    public void receiveMessage(Message<?> message) {
        System.out.println("Received message on contaApplication " + message);

        Message<?> response = null;

        try {
            switch (message.getRequest()) {
                case "listAll":
                    response = handleListAll(message);
                    break;
                case "updateAccount":
                    response = handleUpdateAccount(message);
                    break;
                case "saveAccount":
                    response = handleSaveAccount(message);
                    break;
                case "deleteAccount":
                    response = handleDeleteAccount(message);
                    break;
                case "requestManagerWithLeastAccounts":
                    response = handleRequestManagerWithLeastAccounts(message);
                    break;
                case "requestAccount":
                    response = handleRequestAccount(message);
                    break;  
                case "updateLimit":
                    response = handleUpdateLimit(message);
                    break;   
                case "requestPending":
                    response = handleRequestPending(message);
                    break;  
                case "requestAllFromManager":
                    response = handleRequestAllFromManager(message);
                    break;    
                case "requestTop3":
                    response = handleRequestTop3(message);
                    break; 
                case "requestAllAccountsByManager":
                    response = handleRequestAllAccountsByManager(message);
                    break; 
                case "requestManagerWithMostAccounts":
                    response = handleRequestManagerWithMostAccounts(message);
                    break; 
                case "updateAccountByManager":
                    response = handleUpdateAccountByManager(message);
                    break; 
                case "updateManager":
                    response = handleUpdateManager(message);
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

    private Message<ContaDTO> handleListAll(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        List<ContaDTO> list = queryService.listar();

        if (list != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setList(list);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<ContaDTO> handleUpdateAccount(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<ContaDTO> novo = (GenericData<ContaDTO>) message.getData();
        ContaDTO salvo = commandService.atualizarPorId_cliente(Transformer.transform(novo.getDto(), ContaDTO.class));

        if (salvo != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, ContaDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<ContaDTO> handleUpdateLimit(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        GenericData<ClienteDTO> novo = (GenericData<ClienteDTO>) message.getData();
        ContaDTO salvo = commandService.atualizarLimite(Transformer.transform(novo.getDto(), ClienteDTO.class));

        if (salvo != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, ContaDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<ContaDTO> handleSaveAccount(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        GenericData<?> novo = message.getData();
        ContaDTO salvo = commandService.salvar(Transformer.transform(novo.getDto(), ContaDTO.class));

        if (salvo != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, ContaDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<ContaDTO> handleDeleteAccount(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<ContaDTO> conta = (GenericData<ContaDTO>) message.getData();

        if (commandService.deletarPorId(conta.getDto().getId())) {
            response.setData(conta);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<String> handleRequestManagerWithLeastAccounts(Message<?> message) {
        
        @SuppressWarnings("unchecked")
        GenericData<String> lista = (GenericData<String>) message.getData();
        List<String> lista_id_gerente = lista.getList();

        Map<String, Integer> mapManagerAgg = repo.groupByManager().stream()
            .collect(Collectors.toMap(
                GerenteContaAggregation::getId_gerente,
                GerenteContaAggregation::getAccount_count
            ));

        Map<String, Integer> resultMerge = lista_id_gerente.stream()
            .collect(Collectors.toMap(
                id -> id,
                id -> mapManagerAgg.getOrDefault(id, 0)
            ));

        Message<String> response = new Message<>();
        GenericData<String> data = new GenericData<>();
        Optional<Map.Entry<String, Integer>> managerWithLeastAccounts = resultMerge.entrySet().stream()
            .min(Map.Entry.comparingByValue());

        if (managerWithLeastAccounts.isPresent()) {
            data.setDto(managerWithLeastAccounts.get().getKey());
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<ContaDTO> handleRequestAccount(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<ClienteDTO> cliente = (GenericData<ClienteDTO>) message.getData();

        Optional<Conta> buscado = queryService.buscarPorId_cliente(cliente.getDto().getUuid());
        if(buscado.isPresent()){
            GenericData<ContaDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(buscado.get(), ContaDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }

        return response;
    }

    private Message<String> handleRequestManagerWithMostAccounts(Message<?> message) {
        Message<String> response = new Message<>();

        String buscado = queryService.buscarGerenteComMaisContas();
        if(!buscado.isEmpty()){
            GenericData<String> data = new GenericData<>();
            data.setDto(buscado);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }

        return response;
    }

    private Message<ContaDTO> handleRequestPending(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        GenericData<String> id_gerente = (GenericData<String>) message.getData();
        List<ContaDTO> list = queryService.listarPendentes(id_gerente.getDto());

        if (list != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setList(list);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        
        return response;
    }

    private Message<ContaDTO> handleRequestAllFromManager(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        GenericData<String> id_gerente = (GenericData<String>) message.getData();
        List<ContaDTO> list = queryService.listarPorGerente(id_gerente.getDto());

        if (list != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setList(list);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        
        return response;
    }

    private Message<ContaDTO> handleRequestTop3(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        GenericData<String> id_gerente = (GenericData<String>) message.getData();
        List<ContaDTO> list = queryService.buscarTop3(id_gerente.getDto());

        if (list != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setList(list);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        
        return response;
    }

    private Message<TelaInicialDTO> handleRequestAllAccountsByManager(Message<?> message) {
        Message<TelaInicialDTO> response = new Message<>();
        GenericData<String> id_gerente = (GenericData<String>) message.getData();
        List<TelaInicialDTO> list = queryService.listarContasParaTelaInicial();

        if (list != null) {
            GenericData<TelaInicialDTO> data = new GenericData<>();
            data.setList(list);
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        
        return response;
    }

    private Message<ContaDTO> handleUpdateAccountByManager(Message<?> message) {
        Message<ContaDTO> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<String> novo = (GenericData<String>) message.getData();
        ContaDTO salvo = commandService.atualizarPorId_gerente(novo.getList());

        if (salvo != null) {
            GenericData<ContaDTO> data = new GenericData<>();
            data.setDto(Transformer.transform(salvo, ContaDTO.class));
            response.setData(data);
        } else {
            response.setData(null);
            response.setRequest("error");
        }
        return response;
    }

    private Message<Integer> handleUpdateManager(Message<?> message) {
        Message<Integer> response = new Message<>();
        @SuppressWarnings("unchecked")
        GenericData<String> novo = (GenericData<String>) message.getData();
        Integer salvo = commandService.atualizarGerente(novo.getList());

        if (salvo != -1) {
            GenericData<Integer> data = new GenericData<>();
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
        System.out.println(response);
    }
}
