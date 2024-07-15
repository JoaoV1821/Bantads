import { Component, OnInit, ViewChild } from '@angular/core';
import { AdministradorService } from '../../../services';
import { ActivatedRoute, Router } from '@angular/router';
import { Gerente } from '../../../../shared';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-inserir-editar-gerente',
  templateUrl: './inserir-editar-gerente.component.html',
  styleUrl: './inserir-editar-gerente.component.css'
})
export class InserirEditarGerenteComponent implements OnInit {
  
  @ViewChild('gerenteForm') formGerente! : NgForm;
  gerente : Gerente = new Gerente();
  
  constructor(
    private administradorService : AdministradorService,
    private router : Router,
    private route : ActivatedRoute
  ) {}

  ngOnInit(): void {
    let id = this.route.snapshot.params['id'];
    if(id !== 'novo'){
      id = Number.parseInt(id);
      const res = this.administradorService.buscarPorId(id);
      if(res !== undefined){
        this.gerente = res;
      }
    }
  }

  get editando() {
    return Boolean(this.gerente.id);
  }

  salvar() {
    if (this.editando) {
      this.atualizar();
    } else {
      this.inserir();
    }
  }

  inserir() : void {
    if(this.formGerente.form.valid){
      this.administradorService.inserir(this.gerente);
      this.router.navigate(['/administrador/gerentes']);
    }
  }

  atualizar() : void {
    if(this.formGerente.form.valid){
      this.administradorService.atualizar(this.gerente);
      this.router.navigate(['/administrador/gerentes']);
    }
  }

}
