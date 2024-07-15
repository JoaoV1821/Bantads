import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-deposito',
  templateUrl: './deposito.component.html',
  styleUrl: './deposito.component.css'
})
export class DepositoComponent {
  isVisible: boolean = true;
  saldo: string = '1000,00';
  submitted: boolean = false;
  valid: boolean = false;
  valorSaque!: number;

  constructor(private  fb: FormBuilder) {}

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
    if (this.form.invalid) {

      return;
    } else {
      alert('Depósito realizado com sucesso!')
    }
  }

  onReset(): void {
    this.submitted = false;
    this.form.reset();
  }
}
