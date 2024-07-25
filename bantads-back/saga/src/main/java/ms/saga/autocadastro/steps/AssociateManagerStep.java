package ms.saga.autocadastro.steps;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import reactor.core.publisher.Mono;

public class AssociateManagerStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;


    public AssociateManagerStep(Producer producer) {
        this.producer = producer;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        
        //request procurar gerente
        //confirmar resposta
        //request alterar conta com id do gerente
        //confirmar resposta

        return Mono.empty();
    }

    @Override
    public Mono<Boolean> revert(){

        //request alterar conta com id do gerente null
        //confirmar resposta

        return Mono.empty();
    }

}
