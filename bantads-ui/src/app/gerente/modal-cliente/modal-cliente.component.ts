import { Component, OnInit, Input } from '@angular/core';
import { Cliente } from '../../shared';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-modal-cliente',
  templateUrl: './modal-cliente.component.html',
  styleUrl: './modal-cliente.component.css'
})
export class ModalClienteComponent {

  @Input() cliente!: Cliente;
  constructor(public activeModal : NgbActiveModal){}
  
}
