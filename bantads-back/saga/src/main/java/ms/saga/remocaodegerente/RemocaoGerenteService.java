package ms.saga.remocaodegerente;

import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.saga.dtos.InsercaoGerenteRequestDTO;
import ms.saga.dtos.InsercaoGerenteResponseDTO;
import ms.saga.dtos.RemocaoGerenteResponseDTO;
import ms.saga.dtos.enums.SagaStatus;
import ms.saga.insercaodegerente.steps.AssociateManagerStep;
import ms.saga.insercaodegerente.steps.InsertManagerStep;
import ms.saga.rabbit.Producer;
import ms.saga.remocaodegerente.steps.DeleteManagerStep;
import ms.saga.remocaodegerente.steps.UpdateManagerOnAccountsStep;
import ms.saga.util.Transformer;
import ms.saga.workflow.Workflow;
import ms.saga.workflow.WorkflowException;
import ms.saga.workflow.WorkflowStep;
import ms.saga.workflow.WorkflowStepStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.GerenteDTO;

@Service
public class RemocaoGerenteService {
    
    @Autowired Producer producer;

    public Mono<RemocaoGerenteResponseDTO> remocaoGerente(final String requestDTO){

        Workflow insercaoGerenteWorkflow = this.getRemocaoGerenteWorkflow(requestDTO);
        return Flux.fromStream(() -> insercaoGerenteWorkflow.getSteps().stream())
            .concatMap(WorkflowStep::process)
            .handle(((aBoolean, synchronousSink) -> {
                if(aBoolean){
                    synchronousSink.next(true);
                }
                else
                    synchronousSink.error(new WorkflowException("Remoção de Gerente failed"));
            }))
            .then(Mono.fromCallable(() -> getResponseDTO(requestDTO, SagaStatus.COMPLETED)))
            .onErrorResume(ex -> this.revertRemocaoGerente(insercaoGerenteWorkflow, requestDTO));
    }

    public Mono<RemocaoGerenteResponseDTO> revertRemocaoGerente(final Workflow workflow, final String requestDTO){
        
        System.out.println("revertRemocaoGerente::RemocaoGerenteService");

        return Flux.fromStream(() -> workflow.getSteps().stream())
            .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE))
            .concatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(this.getResponseDTO(requestDTO, SagaStatus.CANCELLED)));
    }

    private Workflow getRemocaoGerenteWorkflow(String requestDTO){
        //REQUISITAR GERENTE COM MENOS CONTAS
        Message msg = new Message<>(UUID.randomUUID().toString(),
        "requestManagerForNewAccount", null , "gerente", "saga.response");
        
        Mono<GenericData<?>> gerenteResponse = producer.sendRequest(msg);
 
        GerenteDTO newManager = gerenteResponse.map(response -> {
            GenericData<GerenteDTO> gerente = Transformer.transform(response, GenericData.class);
            System.out.println("gerente_id " + gerente);
            return gerente.getDto();
        }).block();

        if(newManager == null) return null;

        //REQUISITAR GERENTE A SER DELETADO
        GenericData<String> gerenteData = new GenericData<String>();
        gerenteData.setDto(requestDTO);
        Message<String> msgOld = new Message<String>(UUID.randomUUID().toString(),
        "requestManager", gerenteData , "gerente", "saga.response");
        
        Mono<GenericData<?>> gerenteResponseOld = producer.sendRequest(msgOld);
 
        GerenteDTO oldManager = gerenteResponseOld.map(response -> {
            GenericData<GerenteDTO> gerente = Transformer.transform(response, GenericData.class);
            System.out.println("gerente " + gerente);
            return gerente.getDto();
        }).block();

        if(oldManager == null) return null;

        WorkflowStep updateManagerOnAccountStep = new UpdateManagerOnAccountsStep(producer, newManager, oldManager);
        WorkflowStep deleteManagerStep = new DeleteManagerStep(producer, oldManager);

        return new RemocaoGerenteWorkflow(
            List.of(updateManagerOnAccountStep, deleteManagerStep));
    }

    private RemocaoGerenteResponseDTO getResponseDTO(String requestDTO, SagaStatus status){
        RemocaoGerenteResponseDTO responseDTO = new RemocaoGerenteResponseDTO();
        responseDTO.setId_gerente(requestDTO);
        responseDTO.setStatus(status);
        return responseDTO;
    }

}
