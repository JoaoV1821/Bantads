import { Component } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ViewChild, OnInit } from '@angular/core';
import { AutocadastroService } from '../services';
import { Formulario } from '../../shared';
@Component({
  selector: 'app-autocadastro',
  templateUrl: './autocadastro.component.html',
  styleUrl: './autocadastro.component.css'
})

export class AutocadastroComponent implements OnInit{

  @ViewChild('autocadastroForm') autocadastroForm!: NgForm;
  @ViewChild('cep') cepModel!: NgModel;
  @ViewChild('cidade') cidadeModel!: NgModel;
  @ViewChild('estado') estadoModel!: NgModel;
  @ViewChild('logradouro') logradouroModel!: NgModel;


  solicitado : boolean = false;

  constructor(
    private autocadastroService : AutocadastroService
  ){}

  ngOnInit(): void { 
    this.solicitado = false;
  }

  requestCep() : void {
    let cep = this.cepModel.value;
    this.autocadastroService.getCep(cep).subscribe(
      (value : any) => {
        this.cidadeModel.control.setValue(value.localidade);
        this.estadoModel.control.setValue(value.uf);
        this.logradouroModel.control.setValue(value.logradouro);
      }
    )
  }

  solicitarCadastro(form: NgForm) {
    this.solicitado = true;    
    let dadosForm : Formulario = form.value;
    this.autocadastroService.solicitarCadastro(dadosForm);
  }
}
