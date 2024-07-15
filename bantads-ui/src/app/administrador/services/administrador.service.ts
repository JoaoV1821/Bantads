import { Injectable } from '@angular/core';
import { Cliente, Gerente, Endereco } from '../../shared';

const LS_CLIENTES : string = "clientes";
const LS_GERENTES : string = "gerentes";

@Injectable({
  providedIn: 'root'
})
export class AdministradorService {

  constructor() { }

  insertClientes(count : number) : void {
    this.removerTodosClientes();
    let endereco = new Endereco('1000011', 'Curitiba', 'PR', 'Rua Parana', '111', '');
    let gerente = new Gerente(1, 'Gerente do Rafael I', '20220220222', 'gerente@gmail.com', '41888887777');
    let cliente: Cliente = new Cliente(
      new Date().getTime() ,'Rafael Pedroso I', '10110110111', '41999998888', 'rafaelpedroso@gmail.com', '10000', endereco, gerente, 100000, 50
    );
    const clientes = this.listarTodosClientes();
    for(let i = 1; i <= count; i++){
      clientes.push(cliente);
    }
    localStorage[LS_CLIENTES] = JSON.stringify(clientes);
  }

  removerTodosClientes() : void {
    localStorage[LS_CLIENTES] = [];
  }
  
  listarTodosClientes() : Cliente[] {
    const clientes = localStorage[LS_CLIENTES];
    return clientes ? JSON.parse(clientes) : [];
  }

  listarTodosGerentes() : Gerente [] {
    const gerentes = localStorage[LS_GERENTES];
    return gerentes ? JSON.parse(gerentes) : [];
  }
  inserir(gerente : Gerente) : void {
    const gerentes = this.listarTodosGerentes();
    gerente.id = new Date().getTime();
    gerentes.push(gerente);
    localStorage[LS_GERENTES] = JSON.stringify(gerentes);
  }
  buscarPorId(id : number) : Gerente | undefined {
    const gerentes : Gerente [] = this.listarTodosGerentes();
    return gerentes.find(gerente => gerente.id === id);
  }
  atualizar(gerente : Gerente) : void {
    const gerentes : Gerente[] = this.listarTodosGerentes();
    gerentes.forEach( (obj,index,objs) => {
      if (gerente.id === obj.id){
        objs[index] = gerente
      }
    })
    localStorage[LS_GERENTES] = JSON.stringify(gerentes);
  }
  remover(id : number) : void {
    let gerentes : Gerente[] = this.listarTodosGerentes();
    gerentes = gerentes.filter(gerente => gerente.id !== id);
    localStorage[LS_GERENTES] = JSON.stringify(gerentes);
  }

}
