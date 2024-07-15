import { Component, OnInit } from '@angular/core';
import { Gerente } from '../../../../shared';
import { AdministradorService } from '../../../services';

@Component({
  selector: 'app-listar-gerente',
  templateUrl: './listar-gerente.component.html',
  styleUrl: './listar-gerente.component.css'
})
export class ListarGerenteComponent implements OnInit{

  gerentes: Gerente[]  = [];

  constructor(
    private administradorService : AdministradorService
  ) {}

  ngOnInit(): void {
    this.gerentes = this.listarTodos();
  }

  listarTodos() : Gerente[] {
    return this.administradorService.listarTodosGerentes();
  }

  remover($event : any, gerente : Gerente) : void{
    $event.preventDefault();
    if(confirm(`Deseja remover o Gerente ${gerente.nome}?`)){
      this.administradorService.remover(gerente.id!);
      this.gerentes = this.listarTodos();
    }
  }

}
