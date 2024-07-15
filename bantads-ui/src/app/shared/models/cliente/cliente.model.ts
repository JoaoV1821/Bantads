import { Endereco } from "../endereco";
import { Gerente } from "../gerente";

export class Cliente {

    constructor(
        public id? : number,
        public nome?: string,
        public cpf?: string,
        public telefone?: string,
        public email?: string,
        public salario?: string,
        public endereco?: Endereco,
        public gerente?: Gerente,
        public saldo? : number,
        public limite? : number
    ) { }
}

export interface Formulario {
   
    nome: string,
    cpf: string,
    telefone: string,
    email: string,
    salario: string,
    cep: string,
    cidade: string,
    estado: string,
    logradouro: string,
    numero: string,
    complemento: string
    
}