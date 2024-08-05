package ms.saga.autocadastro;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.saga.autocadastro.steps.AssociateManagerStep;
import ms.saga.autocadastro.steps.CreateAccountStep;
import ms.saga.autocadastro.steps.CreateAuthStep;
import ms.saga.autocadastro.steps.CreateClientStep;
import ms.saga.dtos.AutocadastroRequestDTO;
import ms.saga.dtos.AutocadastroResponseDTO;
import ms.saga.dtos.enums.SagaStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Email;
import ms.saga.util.Transformer;
import ms.saga.workflow.Workflow;
import ms.saga.workflow.WorkflowException;
import ms.saga.workflow.WorkflowStep;
import ms.saga.workflow.WorkflowStepStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shared.GenericData;
import shared.Message;
import shared.dtos.AuthDTO;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;
import shared.dtos.GerenteDTO;

@Service
public class AutocadastroService {
    
    @Autowired Producer producer;

    public Mono<AutocadastroResponseDTO> autocadastro(final AutocadastroRequestDTO requestDTO){
        String uuid = UUID.randomUUID().toString();  // Generate a new UUID for each request
        Workflow autocadastWorkflow = this.getAutocadastWorkflow(requestDTO, uuid);
        return Flux.fromStream(() -> autocadastWorkflow.getSteps().stream())
            .concatMap(WorkflowStep::process)
            .handle(((aBoolean, synchronousSink) -> {
                if(aBoolean){
                    synchronousSink.next(true);
                }
                else
                    synchronousSink.error(new WorkflowException("Autocadastro failed"));
            }))
            .then(Mono.fromCallable(() -> getResponseDTO(requestDTO, SagaStatus.COMPLETED, uuid)))
            .onErrorResume(ex -> this.revertAutocadastro(autocadastWorkflow, requestDTO, uuid));
    }

    public Mono<AutocadastroResponseDTO> revertAutocadastro(final Workflow workflow, final AutocadastroRequestDTO requestDTO, String uuid){
        
        System.out.println("revertAutocadastro::AutocadastroService");

        return Flux.fromStream(() -> workflow.getSteps().stream())
            .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE))
            .concatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(this.getResponseDTO(requestDTO, SagaStatus.CANCELLED, uuid)));
    }

    private Workflow getAutocadastWorkflow(AutocadastroRequestDTO requestDTO, String uuid){

        Message msg = new Message<>(UUID.randomUUID().toString(),
        "requestManagerForNewAccount", null , "gerente", "saga.response");
        
        Mono<GenericData<?>> gerenteResponse = producer.sendRequest(msg);
 
        GerenteDTO gerenteDTO = gerenteResponse.map(response -> {
            GenericData<GerenteDTO> gerente = Transformer.transform(response, GenericData.class);
            System.out.println("gerente " + gerente);
            return gerente.getDto();
        }).block();

        WorkflowStep createClientStep = new CreateClientStep(producer, this.getClientRequestDTO(requestDTO, uuid));
        WorkflowStep createAuthStep = new CreateAuthStep(producer, this.getAuthRequestDTO(requestDTO, uuid));
        WorkflowStep createAccountStep = new CreateAccountStep(producer, this.getContaRequestDTO(requestDTO, uuid));
        WorkflowStep associateManagerStep = new AssociateManagerStep(producer, this.getContaRequestDTO(requestDTO, uuid), gerenteDTO);

        return new AutocadastroWorkflow(
            List.of(createClientStep, createAuthStep, createAccountStep, associateManagerStep));
    }

    private ClienteDTO getClientRequestDTO(AutocadastroRequestDTO requestDTO, String uuid){
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setUuid(uuid);
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
        clienteDTO.setEstado(0);
        return clienteDTO;
    }

    private ContaDTO getContaRequestDTO(AutocadastroRequestDTO requestDTO, String uuid){
        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setId_cliente(uuid);
        Double salario = this.getClientRequestDTO(requestDTO, uuid).getSalario();
        contaDTO.setLimite(salario > 2000.00 ? (Double) (salario / 2) : 0.00);
        contaDTO.setSaldo(0.00);
        contaDTO.setEstado(0);  
        contaDTO.setData(Date.valueOf(LocalDate.now()));
        //contaDTO.setId_gerente(); associateManagerStep faz o update
        return contaDTO;
    }

    private AuthDTO getAuthRequestDTO(AutocadastroRequestDTO requestDTO, String uuid){
        AuthDTO authDTO = new AuthDTO();
        authDTO.setId(uuid);
        authDTO.setEmail(requestDTO.getEmail());
        authDTO.setSenha(null); //senha null ate aprovacao
        authDTO.setTipo("CLIENTE");
        return authDTO;
    }

    private AutocadastroResponseDTO getResponseDTO(AutocadastroRequestDTO requestDTO, SagaStatus status, String uuid){
        AutocadastroResponseDTO responseDTO = new AutocadastroResponseDTO();
        responseDTO = Transformer.transform(requestDTO, AutocadastroResponseDTO.class);
        responseDTO.setStatus(status);
        return responseDTO;
    }

}
