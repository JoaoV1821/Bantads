import { NgModule } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { TelaInicialComponent } from './tela-inicial/tela-inicial/tela-inicial.component';
import { ClientesComponent } from './clientes/clientes/clientes.component';
import { InserirEditarGerenteComponent } from './gerente/inserir-editar-gerente/inserir-editar-gerente/inserir-editar-gerente.component';
import { ListarGerenteComponent } from './gerente/listar-gerente/listar-gerente/listar-gerente.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { NgxCurrencyDirective } from 'ngx-currency';

@NgModule({
  declarations: [
    TelaInicialComponent,
    ClientesComponent,
    InserirEditarGerenteComponent,
    ListarGerenteComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    SharedModule,
    NgxMaskDirective,
    NgxMaskPipe,
    NgxCurrencyDirective,
    CurrencyPipe
  ],
})
export class AdministradorModule { }
