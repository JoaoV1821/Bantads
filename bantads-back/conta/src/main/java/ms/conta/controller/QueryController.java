package ms.conta.controller;


import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import ms.conta.models.dto.MovimentacaoDTO;
import ms.conta.service.QueryService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.format.annotation.DateTimeFormat;



@RestController
@CrossOrigin
@RequestMapping("/conta")
public class QueryController {

    @Autowired QueryService queryService;
    
    @GetMapping("/extrato/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MovimentacaoDTO> extrato(
        @PathVariable Long id,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") Date dataInicio,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") Date dataFim) {
        
            return queryService.extrato(id, dataInicio, dataFim);
    }

    
}
