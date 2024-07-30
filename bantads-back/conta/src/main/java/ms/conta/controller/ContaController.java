package ms.conta.controller;


import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.service.ContaService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.format.annotation.DateTimeFormat;



@RestController
@CrossOrigin
@RequestMapping("/conta")
public class ContaController {

    @Autowired ContaService contaService;

    @PostMapping("/saque/{id}")
    public ResponseEntity<MovimentacaoDTO> saque(@PathVariable String id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.contaService.saque(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/deposito/{id}")
    public ResponseEntity<MovimentacaoDTO> deposito(@PathVariable String id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.contaService.deposito(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/transferencia/{id}")
    public ResponseEntity<MovimentacaoDTO> transferencia(@PathVariable String id, @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO salvo = this.contaService.transferencia(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
    
    @GetMapping("/extrato/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MovimentacaoDTO> extrato(
        @PathVariable Long id,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") Date dataInicio,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") Date dataFim) {
        
            return this.contaService.extrato(id, dataInicio, dataFim);
    }

    
}
