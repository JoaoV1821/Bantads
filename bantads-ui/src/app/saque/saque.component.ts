import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';


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
      alert('Saque realizado com sucesso!')
    }
  }

  onReset(): void {
    this.submitted = false;
    this.form.reset();
  }
}

