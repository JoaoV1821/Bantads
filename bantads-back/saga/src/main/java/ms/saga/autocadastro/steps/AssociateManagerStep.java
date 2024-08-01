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
import shared.dtos.GerenteDTO;

public class AssociateManagerStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final ContaDTO contaDTO;
    private final GerenteDTO gerenteDTO;

    public AssociateManagerStep(Producer producer, ContaDTO contaDTO, GerenteDTO gerenteDTO) {
        this.producer = producer;
        this.contaDTO = contaDTO;
        this.gerenteDTO = gerenteDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        
        System.out.println("AssociateManagerStep::Process");
        //request alterar conta com id do gerente selecionado
        //confirmar resposta
        if(gerenteDTO == null) return Mono.just(null);

        contaDTO.setId_gerente(gerenteDTO.getId());
        GenericData<ContaDTO> data = new GenericData<>();
        data.setDto(contaDTO);

        Message<ContaDTO> msgConta = new Message<ContaDTO>(UUID.randomUUID().toString(),
        "updateAccount", data , "conta", "saga.response");
    
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
        this.contaDTO.setId_gerente(null);

        GenericData<ContaDTO> data = new GenericData<>();
        data.setDto(contaDTO);

        Message<ContaDTO> msg = new Message<ContaDTO>(UUID.randomUUID().toString(),
        "updateAccount", data , "conta", "saga.response");

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
