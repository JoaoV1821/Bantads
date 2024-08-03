package ms.saga.alteracaodeperfil.steps;

import java.util.UUID;

import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import ms.saga.workflow.WorkflowStep;
import ms.saga.workflow.WorkflowStepStatus;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;

public class UpdateClientStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final ClienteDTO clienteDTO;
    private final ClienteDTO old;


    public UpdateClientStep(Producer producer, ClienteDTO clienteDTO, ClienteDTO old) {
        this.producer = producer;
        this.clienteDTO = clienteDTO;
        this.old = old;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        System.out.println("UpdateClient::Process");

        GenericData<ClienteDTO> data = new GenericData<>();
        data.setDto(clienteDTO);

        Message<ClienteDTO> msg = new Message<ClienteDTO>(UUID.randomUUID().toString(),
        "updateClient", data , "cliente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<ClienteDTO> cliente = Transformer.transform(response, GenericData.class);
                System.out.println("cliente" + cliente);
                return cliente.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> revert(){
        System.out.println("UpdateClient::Revert");

        GenericData<ClienteDTO> data = new GenericData<>();
        data.setDto(old);

        Message<ClienteDTO> msg = new Message<ClienteDTO>(UUID.randomUUID().toString(),
        "updateClient", data , "cliente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<ClienteDTO> cliente = Transformer.transform(response, GenericData.class);
                System.out.println("cliente" + cliente);
                return cliente.getDto() != null;
            })
            .onErrorReturn(false);
    }

}
