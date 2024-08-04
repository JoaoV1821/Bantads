package ms.conta.controller;

import javax.mail.MessagingException;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.models.dto.RejeicaoDTO;
import ms.conta.service.CommandService;
import ms.conta.util.Email;
import shared.dtos.ClienteDTO;
import shared.dtos.ContaDTO;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@CrossOrigin
@RequestMapping("/conta")
public class CommandController {

    @Autowired CommandService commandService;

    @PostMapping("/saque/{id}")
    public ResponseEntity<MovimentacaoDTO> saque(@PathVariable Long id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.commandService.saque(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/deposito/{id}")
    public ResponseEntity<MovimentacaoDTO> deposito(@PathVariable Long id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.commandService.deposito(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/transferencia/{id}")
    public ResponseEntity<MovimentacaoDTO> transferencia(@PathVariable Long id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.commandService.transferencia(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/rejeitar-cliente/{id}")
    public ResponseEntity rejeitarCliente(@PathVariable String id, @RequestBody RejeicaoDTO rejeicao) {
        Pair<ClienteDTO, ContaDTO> pair = commandService.rejeitarCliente(id, rejeicao);
        enviarEmailRejeicao(pair.a.getEmail(), rejeicao.getMotivo());

        return pair == null ? ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            : ResponseEntity.status(HttpStatus.OK).body(pair);
    }

    public void enviarEmailRejeicao(String email, String motivo){
        String assunto = "BANTADS - Pedido de Conta rejeitado";
        String msg = "Seu pedido de conta foi rejeitado pelo motivo: " + motivo;
        try {
            Email.enviarEmail(msg, assunto, email);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    
}
