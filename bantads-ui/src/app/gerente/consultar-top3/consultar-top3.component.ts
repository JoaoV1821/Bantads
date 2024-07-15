import { Component, OnInit } from '@angular/core';
import { GerenteService } from '../services';
import { Cliente } from '../../shared';

@Component({
  selector: 'app-consultar-top3',
  templateUrl: './consultar-top3.component.html',
  styleUrl: './consultar-top3.component.css'
})
export class ConsultarTop3Component implements OnInit{

  clientes : Cliente[] = [];
  
  constructor(
    private gerenteService : GerenteService
  ) {}

  ngOnInit(): void {
    this.gerenteService.insertClientes();
    this.clientes = this.listarTop3();
  }

  listarTop3() {
    return this.gerenteService.listarTop3();
  }
}
