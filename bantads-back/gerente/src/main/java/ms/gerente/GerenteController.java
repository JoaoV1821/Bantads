package ms.gerente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import ms.gerente.util.Transformer;
import shared.dtos.GerenteDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@CrossOrigin
@RequestMapping("/gerente")
public class GerenteController {
    
    @Autowired
    private GerenteService gerenteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GerenteDTO> listar() {
        return this.gerenteService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GerenteDTO> listarGerente(@PathVariable String id) {
        GerenteDTO gerente = this.gerenteService.buscarPorId(id);
        return gerente != null ? 
            ResponseEntity.ok().body(Transformer.transform(gerente, GerenteDTO.class)) 
            : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<GerenteDTO> salvar(@RequestBody GerenteDTO dto) {
        GerenteDTO gerenteSalvo = this.gerenteService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(gerenteSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GerenteDTO> atualizar(@PathVariable String id, @RequestBody GerenteDTO dto) {
        GerenteDTO gerenteSalvo = this.gerenteService.atualizar(id, dto);
        return ResponseEntity.ok().body(gerenteSalvo);
    }

    @SuppressWarnings("rawtypes")
    @DeleteMapping("/{id}")
    public ResponseEntity remover(@PathVariable String id){
        
        if(this.gerenteService.remover(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        
    }
    
}