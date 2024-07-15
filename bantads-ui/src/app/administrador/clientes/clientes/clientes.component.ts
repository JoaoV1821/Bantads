import { Component, OnInit } from '@angular/core';
import { AdministradorService } from '../../services';
import { Cliente, Endereco, Gerente } from '../../../shared';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.css'
})
export class ClientesComponent implements OnInit{

  clientes: Cliente[]  = [];

  constructor(
    private administradorService : AdministradorService
  ){}

  ngOnInit(): void {
      this.listarTodos();
  }

  listarTodos() : void {
    this.administradorService.insertClientes(5);
    this.clientes = this.administradorService.listarTodosClientes();
  }


}
