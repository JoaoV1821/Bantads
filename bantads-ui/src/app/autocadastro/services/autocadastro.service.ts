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
    console.log(formData)
    this.http.post(`http://localhost:3000/autocadastro`, formData, {
      headers: {
        "Content-Type": 'application/json'
      }
    }).subscribe(
      response => {
        // Handle successful response here
        console.log('POST request successful', response);
      },
      error => {
        // Handle error response here
        console.error('POST request error', error);
      }
    );
  }

}
