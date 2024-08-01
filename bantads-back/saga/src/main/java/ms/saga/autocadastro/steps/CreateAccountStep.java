package ms.saga.autocadastro.steps;

import java.util.UUID;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ContaDTO;

public class CreateAccountStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final ContaDTO contaDTO;

    public CreateAccountStep(Producer producer, ContaDTO contaDTO) {
        this.producer = producer;
        this.contaDTO = contaDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        
        System.out.println("CreateAccountStep::Process");

        GenericData<ContaDTO> data = new GenericData<>();
        data.setDto(contaDTO);

        Message<ContaDTO> msg = new Message<ContaDTO>(UUID.randomUUID().toString(),
        "saveAccount", data , "conta", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
                System.out.println("conta" + conta);
                return conta.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> revert(){

        System.out.println("CreateAccountStep::Revert");

        GenericData<ContaDTO> data = new GenericData<>();
        data.setDto(contaDTO);

        Message<ContaDTO> msg = new Message<ContaDTO>(UUID.randomUUID().toString(),
        "deleteAccount", data , "conta", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                System.out.println("response" + response);
                GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
                System.out.println("conta" + conta);
                return conta.getDto() != null;
            })
            .onErrorReturn(false);

    }

}
