package ms.saga.insercaodegerente.steps;

import java.util.UUID;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.GerenteDTO;

public class InsertManagerStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final GerenteDTO gerenteDTO;


    public InsertManagerStep(Producer producer, GerenteDTO gerenteDTO) {
        this.producer = producer;
        this.gerenteDTO = gerenteDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){

        System.out.println("InsertManagerStep::Process");
    
        GenericData<GerenteDTO> data = new GenericData<>();
        data.setDto(gerenteDTO);

        Message<GerenteDTO> msg = new Message<GerenteDTO>(UUID.randomUUID().toString(),
        "saveManager", data , "gerente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<GerenteDTO> gerente = Transformer.transform(response, GenericData.class);
                System.out.println("gerente " + gerente);
                return gerente.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);
        
    }

    @Override
    public Mono<Boolean> revert(){

        System.out.println("InsertManagerStep::Revert");

        GenericData<GerenteDTO> data = new GenericData<>();
        data.setDto(gerenteDTO);

        Message<GerenteDTO> msg = new Message<GerenteDTO>(UUID.randomUUID().toString(),
        "deleteManager", data , "gerente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<GerenteDTO> gerente = Transformer.transform(response, GenericData.class);
                System.out.println("gerente" + gerente);
                return gerente.getDto() != null;
            })
            .onErrorReturn(false);

    }

}
