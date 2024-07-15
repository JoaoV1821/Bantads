import { Component, OnInit} from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthGuard } from '../../guard/auth.guard';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent implements OnInit{
  
  showPassword : boolean = false;
  submitted = false;

  form: FormGroup = new FormGroup({
    email: new FormControl(''),
    password:  new FormControl(''),

  })

  email!: string;
  password!: string;

  constructor(private  fb: FormBuilder) {}
    
  
  ngOnInit(): void {
      this.form = this.fb.group(
        {
          email: ['', [Validators.required, Validators.email]],
          password: ['', Validators.required]
        }
      
      )
  }

  get f(): {[key: string]: AbstractControl} { return this.form.controls; }

  togglePassword() {
      this.showPassword = !this.showPassword;
  
    }


  public submit(): void{
    this.submitted = true;
    

    if (this.form.invalid){
      return;
    } else {
      
      window.location.href='/dashboard'
    }
  }  

  onReset(): void {
    this.submitted = false;
    this.form.reset();
  }
}
