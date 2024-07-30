package ms.conta.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ms.conta.models.dto.MovimentacaoDTO;
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
        System.out.println("Received message on contaApplication::ConsumerQuery" + message);

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
                //! ATUALIZAR SALDOS
                handleSaveMovement(message);
                break;
            default:
                return;
        }
    }

    private void handleSaveMovement(Message<?> message) {
        GenericData<MovimentacaoDTO> movimentacao = (GenericData<MovimentacaoDTO>) message.getData();
        queryService.salvar(Transformer.transform(
            movimentacao.getDto(), MovimentacaoDTO.class));
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
