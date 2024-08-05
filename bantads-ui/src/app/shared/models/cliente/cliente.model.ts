import { Endereco } from "../endereco";
import { Gerente } from "../gerente";

export class Cliente {

    constructor(
        public id? : number,
        public nome?: string,
        public email?: string,
        public cpf?: string,
        public telefone?: string,
        public salario?: string,
        public endereco?: Endereco,
        public gerente?: Gerente,
        public saldo? : number,
        public limite? : number
    ) { }
}

export interface Formulario {
   
    nome: string,
    email: string,
    cpf: string,
    telefone: string,
    salario: number,
    logradouro: string,
    numero: string,
    complemento: string
    cep: string,
    cidade: string,
    uf: string,
    
}