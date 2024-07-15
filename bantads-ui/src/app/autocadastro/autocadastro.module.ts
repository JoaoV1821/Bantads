import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AutocadastroComponent } from './autocadastro/autocadastro.component';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { SharedModule } from '../shared/shared.module';
import { NgxCurrencyDirective } from 'ngx-currency';

@NgModule({
  declarations: [
    AutocadastroComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgxMaskPipe,
    SharedModule,
    NgxMaskDirective,
    NgxCurrencyDirective
  ]
})

export class AutocadastroModule { }
