package ms.saga;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ms.saga.autocadastro.AutocadastroService;
import ms.saga.dtos.AutocadastroRequestDTO;
import ms.saga.dtos.AutocadastroResponseDTO;
import ms.saga.util.Email;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@CrossOrigin
@RequestMapping("/saga")
public class SagaController {

    @Autowired AutocadastroService autocadastroService;

    @PostMapping("/autocadastro")
    public Mono<ResponseEntity<AutocadastroResponseDTO>> autocadastro(@RequestBody AutocadastroRequestDTO requestDTO) {
        
        return autocadastroService.autocadastro(requestDTO)
            .map(response -> {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            })
            .onErrorResume(error -> {
                sendEmailInternalError(requestDTO.getEmail());
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
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
