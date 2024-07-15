export class Endereco {

    constructor(
        public cep: string,
        public cidade: string,
        public estado: string,
        public logradouro: string,
        public numero: string,
        public complemento: string
    ) { }
}
