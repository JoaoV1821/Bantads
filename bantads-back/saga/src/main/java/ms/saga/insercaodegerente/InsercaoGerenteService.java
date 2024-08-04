package ms.saga.insercaodegerente;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.saga.dtos.AutocadastroRequestDTO;
import ms.saga.dtos.AutocadastroResponseDTO;
import ms.saga.dtos.InsercaoGerenteRequestDTO;
import ms.saga.dtos.InsercaoGerenteResponseDTO;
import ms.saga.dtos.enums.SagaStatus;
import ms.saga.insercaodegerente.steps.AssociateManagerStep;
import ms.saga.insercaodegerente.steps.InsertManagerStep;
import ms.saga.rabbit.Producer;
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
public class InsercaoGerenteService {
    
    @Autowired Producer producer;

    public Mono<InsercaoGerenteResponseDTO> insercaoGerente(final InsercaoGerenteRequestDTO requestDTO){

        Workflow insercaoGerenteWorkflow = this.getInsercaoGerenteWorkflow(requestDTO);
        return Flux.fromStream(() -> insercaoGerenteWorkflow.getSteps().stream())
            .concatMap(WorkflowStep::process)
            .handle(((aBoolean, synchronousSink) -> {
                if(aBoolean){
                    synchronousSink.next(true);
                }
                else
                    synchronousSink.error(new WorkflowException("Inserção de Gerente failed"));
            }))
            .then(Mono.fromCallable(() -> getResponseDTO(requestDTO, SagaStatus.COMPLETED)))
            .onErrorResume(ex -> this.revertInsercaoGerente(insercaoGerenteWorkflow, requestDTO));
    }

    public Mono<InsercaoGerenteResponseDTO> revertInsercaoGerente(final Workflow workflow, final InsercaoGerenteRequestDTO requestDTO){
        
        System.out.println("revertInsercaoGerente::InsercaoGerenteService");

        return Flux.fromStream(() -> workflow.getSteps().stream())
            .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE))
            .concatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(this.getResponseDTO(requestDTO, SagaStatus.CANCELLED)));
    }

    private Workflow getInsercaoGerenteWorkflow(InsercaoGerenteRequestDTO requestDTO){
        //REQUISITAR GERENTE COM MAIS CONTAS
        Message msg = new Message<>(UUID.randomUUID().toString(),
        "requestManagerWithMostAccounts", null , "conta", "saga.response");
        
        Mono<GenericData<?>> gerenteResponse = producer.sendRequest(msg);
 
        String oldManager = gerenteResponse.map(response -> {
            GenericData<String> gerente = Transformer.transform(response, GenericData.class);
            System.out.println("gerente_id " + gerente);
            return gerente.getDto();
        }).block();

        if(oldManager.isEmpty()) return null;

        WorkflowStep insertManagerStep = new InsertManagerStep(producer, this.getGerenteDTO(requestDTO));
        WorkflowStep associateManagerStep = new AssociateManagerStep(producer, this.getGerenteDTO(requestDTO), oldManager);

        return new InsercaoGerenteWorkflow(
            List.of(insertManagerStep, associateManagerStep));
    }

    private GerenteDTO getGerenteDTO(InsercaoGerenteRequestDTO requestDTO){
        GerenteDTO gerenteDTO = new GerenteDTO();
        gerenteDTO.setId(UUID.randomUUID().toString());
        gerenteDTO.setCpf(requestDTO.getCpf());
        gerenteDTO.setEmail(requestDTO.getEmail());
        gerenteDTO.setTelefone(requestDTO.getTelefone());
        return gerenteDTO;
    }

    private InsercaoGerenteResponseDTO getResponseDTO(InsercaoGerenteRequestDTO requestDTO, SagaStatus status){
        InsercaoGerenteResponseDTO responseDTO = new InsercaoGerenteResponseDTO();
        responseDTO = Transformer.transform(requestDTO, InsercaoGerenteResponseDTO.class);
        responseDTO.setStatus(status);
        return responseDTO;
    }

}
