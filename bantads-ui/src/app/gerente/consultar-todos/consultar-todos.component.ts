import { Component, OnInit } from '@angular/core';
import { GerenteService } from '../services';
import { Cliente } from '../../shared';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalClienteComponent } from '../modal-cliente';

@Component({
  selector: 'app-consultar-todos',
  templateUrl: './consultar-todos.component.html',
  styleUrl: './consultar-todos.component.css'
})
export class ConsultarTodosComponent implements OnInit{
  
  clientes : Cliente[] = [];

  constructor(
    private gerenteService : GerenteService,
    private modalService : NgbModal
  ) {}

  ngOnInit(): void {
      this.gerenteService.insertClientes();
      this.clientes = this.listarTodosClientes();
  }

  listarTodosClientes() : Cliente[] {
    return this.gerenteService.listarTodosClientes();
  }

  handlePesquisa() : void {
    
  }

  abrirModalCliente($event:any,cliente: Cliente) {
    $event.preventDefault();
    const modalRef = this.modalService.open(ModalClienteComponent);
    // this.modalService.open(content, { size: 'xl' });
    modalRef.componentInstance.cliente = cliente;
  }

}
