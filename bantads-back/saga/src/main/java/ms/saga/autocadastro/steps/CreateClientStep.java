package ms.saga.autocadastro.steps;

import java.util.UUID;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import reactor.core.publisher.Mono;
import shared.dtos.ClienteDTO;

public class CreateClientStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final ClienteDTO clienteDTO;


    public CreateClientStep(Producer producer, ClienteDTO clienteDTO) {
        this.producer = producer;
        this.clienteDTO = clienteDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        
        //request criar registro cliente
        //confirmar resposta
        /*Message msg = new Message(UUID.randomUUID().toString(), 
        "updateCliente", clienteDTO, "cliente", "saga.response");    
        
        return producer.sendRequest(msg)
                .map(response -> {
                    System.out.println("response" + response);
                    return checkCliente(response);
                })
                .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
                .onErrorReturn(false);
        */
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> revert(){

        //request apagar registro cliente
        //confirmar resposta

        return Mono.empty();
    }

}
