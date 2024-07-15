import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';


@Component({
  selector: 'app-transferencia',
  templateUrl: './transferencia.component.html',
  styleUrl: './transferencia.component.css'
})

export class TransferenciaComponent {
  isVisible: boolean = true;
  submitted: boolean = false;
  saldo: string = '1000,00';
  conta!: string;
  valor!: string;

  constructor(private fb: FormBuilder) {};

  form: FormGroup = new FormGroup({
      conta: new FormControl('', [Validators.required]),
      valor: new FormControl('', [Validators.required])
      
  })

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        conta: ['', Validators.required, Validators.min(12)],
        valor: ['', Validators.required]
      }
    )
  }

  get f(): {[key: string]: AbstractControl} { return this.form.controls; }
 
  public hideSaldo(): void {
    this.isVisible = !this.isVisible;
    console.log('oi');
    
  }

  public submit() : void{
    this.submitted = true;
    if (this.form.invalid) {
      return;
    } else {
      
      alert('transferência realizada com sucesso!');
    }
  }

  onReset(): void {
    this.submitted = false;
    this.form.reset();
  }
}
