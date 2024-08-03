package ms.saga.remocaodegerente.steps;

import java.util.UUID;

import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import ms.saga.workflow.WorkflowStep;
import ms.saga.workflow.WorkflowStepStatus;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.GerenteDTO;

public class DeleteManagerStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final GerenteDTO oldGerente;


    public DeleteManagerStep(Producer producer, GerenteDTO gerenteDTO) {
        this.producer = producer;
        this.oldGerente = gerenteDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){

        System.out.println("DeleteManagerStep::Process");
    
        GenericData<GerenteDTO> data = new GenericData<>();
        data.setDto(oldGerente);

        Message<GerenteDTO> msg = new Message<GerenteDTO>(UUID.randomUUID().toString(),
        "deleteManager", data , "gerente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<GerenteDTO> gerente = Transformer.transform(response, GenericData.class);
                return gerente.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);
        
    }

    @Override
    public Mono<Boolean> revert(){

        System.out.println("DeleteManagerStep::Revert");

        GenericData<GerenteDTO> data = new GenericData<>();
        data.setDto(oldGerente);

        Message<GerenteDTO> msg = new Message<GerenteDTO>(UUID.randomUUID().toString(),
        "saveManager", data , "gerente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<GerenteDTO> gerente = Transformer.transform(response, GenericData.class);
                return gerente.getDto() != null;
            })
            .onErrorReturn(false);

    }

}
