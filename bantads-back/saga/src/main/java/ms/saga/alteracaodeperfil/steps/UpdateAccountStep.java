package ms.saga.alteracaodeperfil.steps;

import java.util.UUID;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;

public class UpdateAccountStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final ClienteDTO clienteDTO;
    private ContaDTO contaDTO;


    public UpdateAccountStep(Producer producer, ClienteDTO clienteDTO, ContaDTO contaDTO) {
        this.producer = producer;
        this.clienteDTO = clienteDTO;
        this.contaDTO = contaDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){

        System.out.println("UpdateAccount::Process");

        System.out.println(contaDTO);
        if(contaDTO == null) return null;

        GenericData<ClienteDTO> data = new GenericData<>();
        data.setDto(clienteDTO);

        Message<ClienteDTO> msgConta = new Message<ClienteDTO>(UUID.randomUUID().toString(),
        "updateLimit", data , "conta", "saga.response");

        return producer.sendRequest(msgConta)
            .map(response -> {
                GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
                System.out.println("conta" + conta);
                return conta.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> revert(){
        System.out.println("UpdateAccount::Revert");

        GenericData<ContaDTO> data = new GenericData<>();
        data.setDto(contaDTO);

        Message<ContaDTO> msg = new Message<ContaDTO>(UUID.randomUUID().toString(),
        "updateAccount", data , "conta", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
                System.out.println("conta" + conta);
                return conta.getDto() != null;
            })
            .onErrorReturn(false);
    }

    public void convert(Object response){
        GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
        this.contaDTO = conta.getDto();
    }

}
