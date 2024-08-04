package ms.saga.remocaodegerente.steps;

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

public class UpdateManagerOnAccountsStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final GerenteDTO newManager;
    private final GerenteDTO oldManager;

    public UpdateManagerOnAccountsStep(Producer producer, GerenteDTO newManager, GerenteDTO oldManager) {
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
        
        System.out.println("UpdateManagerOnAccountsStep::Process");
    
        if(newManager == null || oldManager == null) return Mono.just(null);

        GenericData<String> data = new GenericData<>();
        data.setList(List.of(newManager.getId(), oldManager.getId()));

        Message<String> msgConta = new Message<String>(UUID.randomUUID().toString(),
        "updateManager", data , "conta", "saga.response");
    
        return producer.sendRequest(msgConta)
            .map(response -> {
                GenericData<String> conta = Transformer.transform(response, GenericData.class);
                System.out.println("conta " + conta);
                return conta.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);

    }

    @Override
    public Mono<Boolean> revert(){
        System.out.println("UpdateManagerOnAccountsStep::Revert");

        GenericData<String> data = new GenericData<>();
        data.setList(List.of(oldManager.getId(), newManager.getId()));

        Message<String> msg = new Message<String>(UUID.randomUUID().toString(),
        "updateManager", data , "conta", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
                return conta.getDto() != null;
            })
            .onErrorReturn(false);

    }

}
