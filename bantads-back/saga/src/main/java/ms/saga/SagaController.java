package ms.saga;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ms.saga.alteracaodeperfil.UpdateProfileService;
import ms.saga.autocadastro.AutocadastroService;
import ms.saga.dtos.AutocadastroRequestDTO;
import ms.saga.dtos.InsercaoGerenteRequestDTO;
import ms.saga.dtos.UpdateProfileRequestDTO;
import ms.saga.dtos.enums.SagaStatus;
import ms.saga.insercaodegerente.InsercaoGerenteService;
import ms.saga.remocaodegerente.RemocaoGerenteService;
import ms.saga.util.Email;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@CrossOrigin
@RequestMapping("/saga")
public class SagaController {

    @Autowired AutocadastroService autocadastroService;
    @Autowired UpdateProfileService updateProfileService;
    @Autowired InsercaoGerenteService insercaoGerenteService;
    @Autowired RemocaoGerenteService remocaoGerenteService;

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

    @PutMapping("/alterar-perfil")
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

    @PostMapping("/inserir-gerente")
    public Mono<Object> insertManager(@RequestBody InsercaoGerenteRequestDTO requestDTO) {
        
        return insercaoGerenteService.insercaoGerente(requestDTO)
        .map(response -> {
            if(response.getStatus() == SagaStatus.COMPLETED){
                return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(response));
            }
            else {
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
            }
        });
    }

    @DeleteMapping("/remover-gerente/{id}")
    public Mono<Object> deleteManager(@PathVariable String id) {
        
        return remocaoGerenteService.remocaoGerente(id)
        .map(response -> {
            if(response.getStatus() == SagaStatus.COMPLETED){
                return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(response));
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
