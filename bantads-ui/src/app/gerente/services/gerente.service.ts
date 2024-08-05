import { Injectable } from '@angular/core';
import { Cliente, Endereco, Gerente } from '../../shared';
import { HttpClient } from '@angular/common/http';

const LS_CLIENTES : string = "clientes";
const enderecosExemplo : Endereco[] = [
  {
    cep: '80000200',
    cidade: 'Curitiba',
    estado: 'PR',
    logradouro: 'Rua Argentina',
    numero: '100',
    complemento: ''
  },
  {
    cep: '1000011',
    cidade: 'Curitiba',
    estado: 'PR',
    logradouro: 'Rua Parana',
    numero: '111',
    complemento: ''
  },
  {
    cep: '80020190',
    cidade: 'Pato Branco',
    estado: 'PR',
    logradouro: 'Avenida Comendador Franco',
    numero: '190',
    complemento: ''
  }
]

const gerenteExemplo = new Gerente(1, 'Gerente do Rafael I', '20220220222', 'gerente@gmail.com', '41888887777');

@Injectable({
  providedIn: 'root'
})
export class GerenteService {

  constructor(private http : HttpClient) { }

  listarTodosClientes() : Cliente[] {
    const clientes = localStorage[LS_CLIENTES];
    return clientes ? JSON.parse(clientes) : [];
  }

  listarTodosParaAprovar(id : string){
    return this.http.get(`localhost:8083/para-aprovar/${id}`);
  }

  listarTop3() : Cliente[] {
    const clientes = localStorage[LS_CLIENTES];
    return clientes ? JSON.parse(clientes) : [];
  }

  buscarPorCpf(cpf : string) : Cliente | undefined {
    const cliente : Cliente [] = this.listarTodosClientes();
    return cliente.find(cliente => cliente.cpf === cpf);
  }

  insertClientes() : void {
    this.removerTodosClientes();
    let cliente: Cliente[] = [
      {
        id: new Date().getTime(),
        nome:'Joao Santos', 
        cpf: '06649958819', 
        telefone: '41966668888', 
        email: 'joaosantos@gmail.com', 
        salario:'50', 
        endereco: enderecosExemplo[2], 
        gerente: gerenteExemplo, 
        saldo: 100000, 
        limite: 50
      },
      {
      id: new Date().getTime()+1,
      nome:'Rafael Pedroso I', 
      cpf: '10110110111', 
      telefone: '41999998888', 
      email: 'rafaelpedroso@gmail.com', 
      salario:'10000', 
      endereco: enderecosExemplo[0], 
      gerente: gerenteExemplo, 
      saldo: 100000, 
      limite: 50
    },
    {
      id: new Date().getTime()+2,
      nome:'Timoteo Chalamet', 
      cpf: '99999999988', 
      telefone: '41898987474', 
      email: 'timoteo@gmail.com', 
      salario:'100000', 
      endereco: enderecosExemplo[1], 
      gerente: gerenteExemplo, 
      saldo: 100000, 
      limite: 50
    }
  ]
    const clientes = this.listarTodosClientes();
    for (let c of cliente) {
      clientes.push(c)
    }
    
    localStorage[LS_CLIENTES] = JSON.stringify(clientes);
  }

  removerTodosClientes() : void {
    localStorage[LS_CLIENTES] = [];
  }

  removerCliente(id : number) : void {
    let clientes : Cliente[] = this.listarTodosClientes();
    clientes = clientes.filter(cliente => cliente.id !== id);
    localStorage[LS_CLIENTES] = JSON.stringify(clientes);
  }
  
}
