package ms.saga.autocadastro.steps;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import reactor.core.publisher.Mono;
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
        
        //request criar registro conta
        //confirmar resposta

        return Mono.empty();
    }

    @Override
    public Mono<Boolean> revert(){

        //request apagar registro contas
        //confirmar resposta

        return Mono.empty();
    }

}
