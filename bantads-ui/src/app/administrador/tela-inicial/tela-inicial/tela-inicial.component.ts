import { Component, OnInit } from '@angular/core';
import { AdministradorService } from '../../services';
import { Gerente } from '../../../shared';

interface GerenteTelaInicial{
    gerente? : string,
    numClientes? : number,
    saldoPositivo? : number,
    saldoNegativo? : number
}
@Component({
  selector: 'app-tela-inicial',
  templateUrl: './tela-inicial.component.html',
  styleUrl: './tela-inicial.component.css'
})
export class TelaInicialComponent implements OnInit {

  gerentes: Gerente[]  = [];
  gerentesTransformed: GerenteTelaInicial[] = [];

  ngOnInit(): void {
      this.transformGerentes();
  }

  constructor(
    private administradorService : AdministradorService
  ) {}

  listarTodos() : Gerente[] {
    return this.administradorService.listarTodosGerentes();
  }

  transformGerentes() : any {
    this.gerentes = this.listarTodos();
    this.gerentes.forEach((ger) => {
      let newGerente : GerenteTelaInicial = {
        gerente: ger.nome, 
        numClientes: Math.floor((Math.random() * 100) + 1),
        saldoPositivo: Math.floor((Math.random() * 10000) + 1),
        saldoNegativo: Math.floor((Math.random() * 10000) + 1)
      };
      this.gerentesTransformed.push(newGerente);
    })
  }

}