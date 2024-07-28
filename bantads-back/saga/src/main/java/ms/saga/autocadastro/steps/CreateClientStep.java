package ms.saga.autocadastro.steps;

import java.util.UUID;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;

public class CreateClientStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final ClienteDTO clienteDTO;


    public CreateClientStep(Producer producer, ClienteDTO clienteDTO) {
        this.producer = producer;
        this.clienteDTO = clienteDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){

        System.out.println("CreateClientStep::Process");
    
        GenericData<ClienteDTO> data = new GenericData<>();
        data.setDto(clienteDTO);

        Message<ClienteDTO> msg = new Message<ClienteDTO>(UUID.randomUUID().toString(),
        "saveClient", data , "cliente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<ClienteDTO> cli = Transformer.transform(response, GenericData.class);
                System.out.println("cli " + cli);
                return cli.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);
        
    }

    @Override
    public Mono<Boolean> revert(){

        System.out.println("CreateClientStep::Revert");

        GenericData<ClienteDTO> data = new GenericData<>();
        data.setDto(clienteDTO);

        Message<ClienteDTO> msg = new Message<ClienteDTO>(UUID.randomUUID().toString(),
        "deleteClient", data , "cliente", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                System.out.println("response" + response);
                GenericData<ClienteDTO> cli = Transformer.transform(response, GenericData.class);
                System.out.println("cli" + cli);
                return cli.getDto() != null;
            })
            .onErrorReturn(false);

    }

}
