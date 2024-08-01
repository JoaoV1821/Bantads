package ms.saga.alteracaodeperfil.steps;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import reactor.core.publisher.Mono;

public class UpdateClientStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;


    public UpdateClientStep(Producer producer) {
        this.producer = producer;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> revert(){
        return Mono.empty();
    }

}
