package ms.saga;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import ms.saga.alteracaodeperfil.UpdateProfileService;
import ms.saga.autocadastro.AutocadastroService;
import ms.saga.dtos.AutocadastroRequestDTO;
import ms.saga.dtos.AutocadastroResponseDTO;
import ms.saga.dtos.UpdateProfileRequestDTO;
import ms.saga.dtos.UpdateProfileResponseDTO;
import ms.saga.dtos.enums.SagaStatus;
import ms.saga.util.Email;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@CrossOrigin
@RequestMapping("/saga")
public class SagaController {

    @Autowired AutocadastroService autocadastroService;
    @Autowired UpdateProfileService updateProfileService;

    @PostMapping("/autocadastro")
    public Mono<Object> autocadastro(@RequestBody AutocadastroRequestDTO requestDTO) {
        
        return autocadastroService.autocadastro(requestDTO)
            .map(response -> {
                if(response.getStatus() == SagaStatus.COMPLETED){
                    return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(response));
                }
                else {
                    sendEmailInternalError(requestDTO.getEmail());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                }
            });
    }

    @PostMapping("/alterar-perfil")
    public Mono<Object> updateProfile(@RequestBody UpdateProfileRequestDTO requestDTO) {
        
        return updateProfileService.updateProfile(requestDTO)
        .map(response -> {
            if(response.getStatus() == SagaStatus.COMPLETED){
                return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(response));
            }
            else {
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
            }
        });
    }

    public void sendEmailInternalError(String email) {
        String assunto = "BANTADS - Cadastro não concluído";
        String msg = "Devido a falhas internas, seu processo de cadastro não pode ser concluído. " 
        + "Contate o suporte para mais informações.";

        try {
            Email.enviarEmail(msg, assunto, email);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    
}
