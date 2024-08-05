import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { LoginDto } from '../../shared/models/login';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(
    private http : HttpClient
  ) { }

  solicitarLogin(formData : LoginDto): Observable<HttpResponse<any>>{
    console.log(formData)
    return this.http.post<any>(`http://localhost:3000/auth/autenticar`, formData, {
      headers: {
        "Content-Type": 'application/json'
      },
      observe: 'response'
    });
  }

}
