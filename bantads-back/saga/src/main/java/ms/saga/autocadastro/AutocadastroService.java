package ms.saga.autocadastro;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ms.saga.Workflow;
import ms.saga.WorkflowException;
import ms.saga.WorkflowStep;
import ms.saga.WorkflowStepStatus;
import ms.saga.autocadastro.steps.AssociateManagerStep;
import ms.saga.autocadastro.steps.CreateAccountStep;
import ms.saga.autocadastro.steps.CreateAuthStep;
import ms.saga.autocadastro.steps.CreateClientStep;
import ms.saga.dtos.OrchestratorRequestDTO;
import ms.saga.dtos.OrchestratorResponseDTO;
import ms.saga.dtos.enums.SagaStatus;
import ms.saga.rabbit.Producer;
import ms.saga.util.Email;
import ms.saga.util.Transformer;
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

    public Mono<OrchestratorResponseDTO> autocadastro(final OrchestratorRequestDTO requestDTO){
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

    public Mono<OrchestratorResponseDTO> revertAutocadastro(final Workflow workflow, final OrchestratorRequestDTO requestDTO, String uuid){
        
        System.out.println("revertAutocadastro::AutocadastroService");
/* 
        String msg = "Devido a falhas internas, seu processo de cadastro não pode ser concluído. " 
        + "Contate o suporte para mais informações.";
        String assunto = "BANTADS - Cadastro não concluído";
        String email = requestDTO.getEmail();

        try {
            Email.enviarEmail(msg, assunto, email);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
*/
        return Flux.fromStream(() -> workflow.getSteps().stream())
            .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE))
            .concatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(this.getResponseDTO(requestDTO, SagaStatus.CANCELLED, uuid)));
    }

    private Workflow getAutocadastWorkflow(OrchestratorRequestDTO requestDTO, String uuid){

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

    private ClienteDTO getClientRequestDTO(OrchestratorRequestDTO requestDTO, String uuid){
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(uuid);
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

    private ContaDTO getContaRequestDTO(OrchestratorRequestDTO requestDTO, String uuid){
        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setId_cliente(uuid);
        Double salario = this.getClientRequestDTO(requestDTO, uuid).getSalario();
        contaDTO.setLimite(salario > 2000.00 ? (Double) (salario / 2) : 0.00);
        contaDTO.setSaldo(0.00);
        contaDTO.setEstado(0);  
        contaDTO.setId(UUID.randomUUID().toString());
        contaDTO.setData(Date.valueOf(LocalDate.now()));
        //contaDTO.setNumero_conta();
        //contaDTO.setId_gerente(); associateManagerStep faz o update
        return contaDTO;
    }

    private AuthDTO getAuthRequestDTO(OrchestratorRequestDTO requestDTO, String uuid){
        AuthDTO authDTO = new AuthDTO();
        authDTO.setId(uuid);
        authDTO.setEmail(requestDTO.getEmail());
        authDTO.setSenha(null); //senha null ate aprovacao
        authDTO.setTipo("CLIENTE");
        return authDTO;
    }

    private OrchestratorResponseDTO getResponseDTO(OrchestratorRequestDTO requestDTO, SagaStatus status, String uuid){
        OrchestratorResponseDTO responseDTO = new OrchestratorResponseDTO();
        responseDTO = Transformer.transform(requestDTO, OrchestratorResponseDTO.class);
        responseDTO.setStatus(status);
        return responseDTO;
    }

}
