package ms.saga.autocadastro.steps;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import reactor.core.publisher.Mono;
import shared.dtos.AuthDTO;

public class CreateAuthStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final AuthDTO AuthDTO;


    public CreateAuthStep(Producer producer, AuthDTO authDTO) {
        this.producer = producer;
        AuthDTO = authDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        
        //request criar registro auth
        //confirmar resposta

        return Mono.empty();
    }

    @Override
    public Mono<Boolean> revert(){

        //request apagar registro auth
        //confirmar resposta

        return Mono.empty();
    }

}
