import { Component, OnInit } from '@angular/core';
import { GerenteService } from '../services';
import { Cliente } from '../../shared';

@Component({
  selector: 'app-consultar-cliente',
  templateUrl: './consultar-cliente.component.html',
  styleUrl: './consultar-cliente.component.css'
})
export class ConsultarClienteComponent implements OnInit {

  pesquisado: Boolean = false;
  cliente! : Cliente | undefined;

  ngOnInit(): void {
      this.pesquisado = false;
  }

  constructor(
    private gerenteService : GerenteService
  ) {}

  buscarCliente(cpf : string) : void {
    this.pesquisado = true;
    this.cliente  = this.gerenteService.buscarPorCpf(cpf);
  }

}
