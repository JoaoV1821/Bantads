import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-saque',
  templateUrl: './saque.component.html',
  styleUrl: './saque.component.css'
})

export class SaqueComponent {
  isVisible: boolean = true;
  saldo: string = '1000,00';
  submitted: boolean = false;
  valid: boolean = false;
  valorSaque!: number;

  constructor(private  fb: FormBuilder, 
    private http : HttpClient) {}

  form: FormGroup = new FormGroup({
    valorSaque: new FormControl('', [Validators.required]),
  })

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        valorSaque: ['', Validators.required],
        
      }
    
    )
}

  get f(): {[key: string]: AbstractControl} { return this.form.controls; }

  public hideSaldo(): void {
    this.isVisible = !this.isVisible;
    
  }

  public submit() : void {
    this.submitted = true;
    let id : number = 3;
    if (this.form.invalid) {

      return;
    } else {
      alert('Saque realizado com sucesso!')
      this.http.post(`http://localhost:3000/saque/${id}`, new Movimentacao(0, '', 'SAQUE', id, undefined, this.valorSaque), {
        headers: {
          "Content-Type": 'application/json'
        }
      }).subscribe(
        response => {
          console.log('POST request successful', response);
        },
        error => {
          console.error('POST request error', error);
        }
      );
    }
  }

  onReset(): void {
    this.submitted = false;
    this.form.reset();
  }
}
class Movimentacao {
  constructor(
      public id? : number,
      public data? : string,
      public tipo? : string,
      public origem? : number,
      public destino? : number,
      public valor? : number
  ){}
}
