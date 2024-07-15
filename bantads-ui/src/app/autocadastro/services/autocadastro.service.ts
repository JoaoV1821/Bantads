import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Cliente, Endereco, Formulario } from '../../shared';

@Injectable({
  providedIn: 'root'
})
export class AutocadastroService {

  constructor(
    private http : HttpClient
  ) { }

  getCep(cep: string) : any {
    return this.http.get(`https://viacep.com.br/ws/${cep}/json/`, {
      headers: {
        "Content-Type": 'application/json'
      }
    });
  }

  solicitarCadastro(formData : Formulario){
    console.log('solicitarCadastro(cliente) :: autocadastro.service');
    let endereco = new Endereco(formData.cep, formData.cidade, formData.estado, formData.logradouro, formData.numero, formData.complemento);
    let cliente = new Cliente(0,formData.nome, formData.cpf, formData.telefone, formData.email, formData.salario, endereco);
    console.log(cliente);
  }

}
