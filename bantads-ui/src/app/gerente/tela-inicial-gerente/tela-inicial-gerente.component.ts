import { Component, OnInit } from '@angular/core';
import { GerenteService } from '../services';
import { Cliente } from '../../shared';

@Component({
  selector: 'app-tela-inicial-gerente',
  templateUrl: './tela-inicial-gerente.component.html',
  styleUrl: './tela-inicial-gerente.component.css'
})
export class TelaInicialGerenteComponent implements OnInit{
  
  clientes : Cliente[] = [];

  constructor(
    private gerenteService : GerenteService
  ) {}
  
  ngOnInit(): void {
    this.gerenteService.insertClientes();
    this.clientes = this.listarTodosClientes();
  }

  listarTodosClientes() : Cliente[] {
    return this.gerenteService.listarTodosClientes();
  }

  aprovarCliente(cliente : Cliente) : void {
    this.gerenteService.removerCliente(cliente.id!);
    this.clientes = this.gerenteService.listarTodosClientes();
  }

  toggleRejeicao(cliente: Cliente): void {
    if (cliente.limite === undefined) {
      this.resetMotivoRejeicao();
      cliente.limite = undefined;
    } else {
      cliente.limite = 0;
    }
  }

  resetMotivoRejeicao(): void {
    this.clientes.forEach(cliente => cliente.limite = undefined);
  }

  rejeitarCliente(cliente: Cliente): void {
      this.gerenteService.removerCliente(cliente.id!);
      this.clientes = this.gerenteService.listarTodosClientes();
  }
}
