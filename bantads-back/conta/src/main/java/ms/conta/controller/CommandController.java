package ms.conta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.service.CommandService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@RequestMapping("/conta")
public class CommandController {

    @Autowired CommandService commandService;

    @PostMapping("/saque/{id}")
    public ResponseEntity<MovimentacaoDTO> saque(@PathVariable String id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.commandService.saque(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/deposito/{id}")
    public ResponseEntity<MovimentacaoDTO> deposito(@PathVariable String id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.commandService.deposito(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/transferencia/{id}")
    public ResponseEntity<MovimentacaoDTO> transferencia(@PathVariable String id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.commandService.transferencia(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
    
}
