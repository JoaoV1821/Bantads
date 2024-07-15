import { Component } from '@angular/core';

interface Transacao {
  dataHora: string;
  operacao: string;
  origemDestino: string;
  valor: number;
}

@Component({
  selector: 'app-consultar-extrato',
  templateUrl: './consultar-extrato.component.html',
  styleUrls: ['./consultar-extrato.component.css']
})
export class ConsultarExtratoComponent {
  extrato: Transacao[] = []; 
  dataInicio: string = ''; 
  dataFim: string = ''; 

  constructor() {
    this.extrato = [
      { dataHora: '20/03/2024 12:54:06', operacao: 'Depósito', origemDestino: 'Conta corrente', valor: 1210 },
      { dataHora: '20/03/2024 12:54:06', operacao: 'Saque', origemDestino: 'Conta corrente', valor: -900 },
      { dataHora: '20/03/2024 12:54:06', operacao: 'Transferência', origemDestino: 'Beltrano Silva', valor: 2000 }
    ];
  }

  calcularSaldo(): number {
    let saldo = 0;
    this.extrato.forEach(transacao => {
      saldo += transacao.valor;
    });
    return saldo;
  }

  getClassForOperacao(transacao: Transacao): string {
    if (transacao.valor < 0) {
      return 'valor-negativo';
    } else if (transacao.operacao === 'Depósito' || transacao.operacao === 'Transferência (entrada)') {
      return 'transacao-entrada';
    } else {
      return '';
    }
  }

  filtrarTransacoes() {
    console.log('Filtrando transações...');
  }
}
