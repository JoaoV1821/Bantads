import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NumericoDirective } from './directives/numerico/numerico.directive';
import { CaixaAltaPipe } from './pipes/caixa-alta/caixa-alta.pipe';
import { CpfPipe } from './pipes/cpf/cpf.pipe';
import { TelefonePipe } from './pipes/telefone/telefone.pipe';
import { StringOnlyDirective } from './directives/string-only/string-only.directive';

@NgModule({
  declarations: [
    NumericoDirective,
    CaixaAltaPipe,
    CpfPipe,
    TelefonePipe,
    StringOnlyDirective
  ],
  imports: [
    CommonModule
  ],
  exports:[
    NumericoDirective,
    CaixaAltaPipe,
    CpfPipe,
    TelefonePipe,
    StringOnlyDirective
  ]
})
export class SharedModule { }
