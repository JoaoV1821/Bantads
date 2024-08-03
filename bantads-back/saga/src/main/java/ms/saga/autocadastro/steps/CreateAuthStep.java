package ms.saga.autocadastro.steps;

import java.util.UUID;

import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.AuthDTO;

public class CreateAuthStep implements WorkflowStep{
    
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;
    private final Producer producer;
    private final AuthDTO authDTO;


    public CreateAuthStep(Producer producer, AuthDTO authDTO) {
        this.producer = producer;
        this.authDTO = authDTO;
    }

    @Override
    public WorkflowStepStatus getStatus(){
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process(){
        
        System.out.println("CreateAuthStep::Process");

        GenericData<AuthDTO> data = new GenericData<>();
        data.setDto(authDTO);

        Message<AuthDTO> msg = new Message<AuthDTO>(UUID.randomUUID().toString(),
        "saveAuth", data , "auth", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                System.out.println("**************************response***************8");
                System.out.println(response);
                GenericData auth = Transformer.transform(response, GenericData.class);
                System.out.println("auth" + auth);
                System.out.println(auth.getDto() != null);
                return auth.getDto() != null;
            })
            .doOnNext(b -> this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED)
            .onErrorReturn(false);
        
    }

    @Override
    public Mono<Boolean> revert(){

        System.out.println("CreateAuthStep::Revert");

        GenericData<AuthDTO> data = new GenericData<>();
        data.setDto(authDTO);

        Message<AuthDTO> msg = new Message<AuthDTO>(UUID.randomUUID().toString(),
        "deleteAuth", data , "auth", "saga.response");

        return producer.sendRequest(msg)
            .map(response -> {
                System.out.println("response" + response);
                GenericData<AuthDTO> auth = Transformer.transform(response, GenericData.class);
                System.out.println("auth" + auth);
                return auth.getDto() != null;
            })
            .onErrorReturn(false);
    }

}
