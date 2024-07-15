import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { AutocadastroModule } from './autocadastro/autocadastro.module';
import { LoginModule } from './login/login.module';
import { SharedModule } from './shared/shared.module';
import { AdministradorModule } from './administrador/administrador.module';
import { GerenteModule } from './gerente';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { provideNgxMask, NgxMaskDirective } from 'ngx-mask';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SaqueComponent } from './saque/saque.component';
import { TransferenciaComponent } from './transferencia/transferencia.component';
import { DepositoComponent } from './deposito/deposito.component';
import { ConsultarExtratoComponent } from './consultar-extrato/consultar-extrato.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './guard/auth.guard';



@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    SaqueComponent,
    TransferenciaComponent,
    DepositoComponent,
    ConsultarExtratoComponent,
    HomeComponent,
  
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    AppRoutingModule,
    LoginModule,
    AutocadastroModule,
    FormsModule,
    AutocadastroModule,
    SharedModule,
    GerenteModule,
    NgbModule,
    NgxMaskDirective,
    AdministradorModule,
  ],

  providers: [
    provideHttpClient(),
    provideNgxMask(),
    AuthGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
