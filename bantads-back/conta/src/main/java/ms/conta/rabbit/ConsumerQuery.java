package ms.conta.rabbit;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.models.dto.QueryUpdateDTO;
import ms.conta.service.QueryService;
import ms.conta.util.Transformer;
import shared.GenericData;
import shared.Message;
import shared.dtos.ContaDTO;

@Component
@SuppressWarnings("unchecked")
public class ConsumerQuery {

    @Autowired QueryService queryService;
    
    @RabbitListener(queues = "query") 
    public void receiveMessage(Message<?> message) {
        System.out.println("**********************************");
        System.out.println("CONSUMER QUERY");
        System.out.println("Received message on contaApplication::ConsumerQuery " + message);

        try {
            
            switch (message.getRequest()) {
                case "updateAccount":
                    handleUpdateAccount(message);
                    break;
                case "saveAccount":
                    handleSaveAccount(message);
                    break;
                case "deleteAccount":
                    handleDeleteAccount(message);
                    break;
                case "saveMovement":
                    handleSaveMovement(message);
                    break;
                default:
                    return;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private void handleSaveMovement(Message<?> message) {
        GenericData<QueryUpdateDTO> movimentacao = (GenericData<QueryUpdateDTO>) message.getData();
        if(movimentacao.getDto().getOrigem() != null)
            queryService.atualizar(movimentacao.getDto().getOrigem());
        
        if(movimentacao.getDto().getDestino() != null)
            queryService.atualizar(movimentacao.getDto().getDestino());

        queryService.salvar(Transformer.transform(
            movimentacao.getDto().getMovimentacao(), MovimentacaoDTO.class));
    }

    private void handleUpdateAccount(Message<?> message) {
        GenericData<ContaDTO> novo = (GenericData<ContaDTO>) message.getData();
        queryService.atualizar(Transformer.transform(
            novo.getDto(), ContaDTO.class));
    }

    private void handleSaveAccount(Message<?> message) {
        GenericData<ContaDTO> novo = (GenericData<ContaDTO>) message.getData();
        queryService.salvar(Transformer.transform(novo.getDto(), ContaDTO.class));
    }

    private void handleDeleteAccount(Message<?> message) {
        GenericData<ContaDTO> conta = (GenericData<ContaDTO>) message.getData();
        queryService.deletar(conta.getDto());
        
    }

}
