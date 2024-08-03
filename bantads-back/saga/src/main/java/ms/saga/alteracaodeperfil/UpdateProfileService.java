package ms.saga.alteracaodeperfil;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.saga.Workflow;
import ms.saga.WorkflowException;
import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.alteracaodeperfil.steps.UpdateAccountStep;
import ms.saga.alteracaodeperfil.steps.UpdateClientStep;
import ms.saga.dtos.UpdateProfileRequestDTO;
import ms.saga.dtos.UpdateProfileResponseDTO;
import ms.saga.dtos.enums.SagaStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Transformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;

@Service
public class UpdateProfileService {
    
    @Autowired
    private Producer producer;

    public Mono<UpdateProfileResponseDTO> updateProfile(final UpdateProfileRequestDTO requestDTO){
        
        Workflow updateProfileWorkflow = this.getUpdateProfileWorkflow(requestDTO);
        return Flux.fromStream(() -> updateProfileWorkflow.getSteps().stream())
            .concatMap(WorkflowStep::process)
            .handle(((aBoolean, synchronousSink) -> {
                if(aBoolean){
                    synchronousSink.next(true);
                }
                else
                    synchronousSink.error(new WorkflowException("Update Profile failed"));
            }))
            .then(Mono.fromCallable(() -> getResponseDTO(requestDTO, SagaStatus.COMPLETED)))
            .onErrorResume(ex -> this.revertUpdateProfile(updateProfileWorkflow, requestDTO));
    }

    public Mono<UpdateProfileResponseDTO> revertUpdateProfile(final Workflow workflow, final UpdateProfileRequestDTO requestDTO){
        
        return Flux.fromStream(() -> workflow.getSteps().stream())
            .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE))
            .concatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(this.getResponseDTO(requestDTO, SagaStatus.CANCELLED)));
    }

    private Workflow getUpdateProfileWorkflow(UpdateProfileRequestDTO requestDTO){

        GenericData<ClienteDTO> data = new GenericData<>();
        data.setDto(Transformer.transform(requestDTO, ClienteDTO.class));
        Message msg = new Message<>(UUID.randomUUID().toString(),
        "requestClient", data , "cliente", "saga.response");
        
        Mono<GenericData<?>> clienteResponse = producer.sendRequest(msg);
 
        ClienteDTO clienteOldDTO = clienteResponse.map(response -> {
            GenericData<ClienteDTO> clienteOld = Transformer.transform(response, GenericData.class);
            System.out.println("ClienteOld " + clienteOld);
            return clienteOld.getDto();
        }).block();

        if(clienteOldDTO == null) return null;

        GenericData<ClienteDTO> dataOldAccount = new GenericData<>();
        dataOldAccount.setDto(this.getClientRequestDTO(requestDTO));
        Message msgConta = new Message<>(UUID.randomUUID().toString(),
        "requestAccount", dataOldAccount , "conta", "saga.response");
        
        Mono<GenericData<?>> contaResponse = producer.sendRequest(msgConta);
 
        ContaDTO contaDTO = contaResponse.map(response -> {
            GenericData<ContaDTO> conta = Transformer.transform(response, GenericData.class);
            System.out.println("conta" + conta);
            return conta.getDto();
        }).block();

        WorkflowStep updateClientStep = new UpdateClientStep(producer, this.getClientRequestDTO(requestDTO), clienteOldDTO);
        if(requestDTO.getSalario() != clienteOldDTO.getSalario()){
            WorkflowStep updateAccountStep = new UpdateAccountStep(producer, this.getClientRequestDTO(requestDTO), contaDTO);
            return new UpdateProfileWorkflow(List.of(updateClientStep, updateAccountStep));
        }

        return new UpdateProfileWorkflow(List.of(updateClientStep));
    }

    private ClienteDTO getClientRequestDTO(UpdateProfileRequestDTO requestDTO){
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setUuid(requestDTO.getId());
        clienteDTO.setNome(requestDTO.getNome());
        clienteDTO.setEmail(requestDTO.getEmail());
        clienteDTO.setCpf(requestDTO.getCpf());
        clienteDTO.setTelefone(requestDTO.getTelefone());
        clienteDTO.setSalario(requestDTO.getSalario());
        clienteDTO.setLogradouro(requestDTO.getLogradouro());
        clienteDTO.setNumero(requestDTO.getNumero());
        clienteDTO.setComplemento(requestDTO.getComplemento());
        clienteDTO.setCep(requestDTO.getCep());
        clienteDTO.setCidade(requestDTO.getCidade());
        clienteDTO.setUf(requestDTO.getUf());
        clienteDTO.setEstado(requestDTO.getEstado());
        return clienteDTO;
    }

    private UpdateProfileResponseDTO getResponseDTO(UpdateProfileRequestDTO requestDTO, SagaStatus status){
        UpdateProfileResponseDTO responseDTO = new UpdateProfileResponseDTO();
        responseDTO = Transformer.transform(requestDTO, UpdateProfileResponseDTO.class);
        responseDTO.setStatus(status);
        return responseDTO;
    }

}
