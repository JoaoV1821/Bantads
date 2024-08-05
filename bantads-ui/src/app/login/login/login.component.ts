import { Component, OnInit} from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthGuard } from '../../guard/auth.guard';
import { LoginDto } from '../../shared/models/login';
import { LoginService } from '../services';
import { HttpResponse } from '@angular/common/http';

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

  constructor(private  fb: FormBuilder, private loginService: LoginService) {}
    
  
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
      const loginDto : LoginDto = {
        email: this.form.get('email')?.value,
        password: this.form.get('password')?.value
      };
      this.loginService.solicitarLogin(loginDto).subscribe(
        (response: HttpResponse<any>) => {
          if (response.status === 200) {
            console.log('Login efetuado com sucesso', response.body);
            window.location.href = '/dashboard';
          } else {
            console.log('Unexpected status code', response.status, response.body);
          }
        }
      );
      window.location.href='/dashboard'
    }
  }  

  onReset(): void {
    this.submitted = false;
    this.form.reset();
  }
}
