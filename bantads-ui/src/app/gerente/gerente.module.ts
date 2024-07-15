import { NgModule } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { TelaInicialGerenteComponent } from './tela-inicial-gerente/tela-inicial-gerente.component';
import { SharedModule } from '../shared/shared.module';
import { FormsModule } from '@angular/forms';
import { ConsultarClienteComponent } from './consultar-cliente/consultar-cliente.component';
import { ConsultarTodosComponent } from './consultar-todos/consultar-todos.component';
import { ConsultarTop3Component } from './consultar-top3/consultar-top3.component';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { ModalClienteComponent } from './modal-cliente/modal-cliente.component';



@NgModule({
  declarations: [
    TelaInicialGerenteComponent,
    ConsultarClienteComponent,
    ConsultarTodosComponent,
    ConsultarTop3Component,
    ModalClienteComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    NgxMaskDirective,
    NgxMaskPipe,
    CurrencyPipe
  ]
})
export class GerenteModule { }
