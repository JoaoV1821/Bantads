package ms.saga.insercaodegerente.steps;

import java.util.List;
import java.util.UUID;

import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import ms.saga.workflow.WorkflowStep;
import ms.saga.workflow.WorkflowStepStatus;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ContaDTO;
import shared.dtos.GerenteDTO;

public class AssociateManagerStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final GerenteDTO newManager;
    private final String oldManager;

    public AssociateManagerStep(Producer producer, GerenteDTO newManager, String oldManager) {
        this.producer = producer;
        this.newManager = newManager;
        this.oldManager = oldManager;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        
        System.out.println("AssociateManagerStep::Process");
        if(newManager == null || oldManager == null) return Mono.just(null);

        GenericData<String> data = new GenericData<>();
        data.setList(List.of(newManager.getId(), oldManager));

        Message<String> msgConta = new Message<String>(UUID.randomUUID().toString(),
        "updateAccountByManager", data , "conta", "saga.response");
    
        return producer.sendRequest(msgConta)
            .map(response -> {
                GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
                System.out.println("conta " + conta);
                return conta.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);

    }

    @Override
    public Mono<Boolean> revert(){

        System.out.println("AssociateManagerStep::Revert");

        GenericData<String> data = new GenericData<>();
        data.setList(List.of(oldManager, newManager.getId()));

        Message<String> msgConta = new Message<String>(UUID.randomUUID().toString(),
        "updateAccountByManager", data , "conta", "saga.response");
    
        return producer.sendRequest(msgConta)
            .map(response -> {
                GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
                System.out.println("conta " + conta);
                return conta.getDto() != null;
            })
            .onErrorReturn(false);

    }

}
