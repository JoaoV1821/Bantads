import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AutocadastroComponent } from './autocadastro/autocadastro/autocadastro.component';
import { LoginComponent } from './login/login/login.component';
import { ClientesComponent, InserirEditarGerenteComponent, ListarGerenteComponent, TelaInicialComponent } from './administrador';
import { ConsultarTop3Component, TelaInicialGerenteComponent, ConsultarClienteComponent, ConsultarTodosComponent } from './gerente';
import { SaqueComponent } from './saque/saque.component';
import { TransferenciaComponent } from './transferencia/transferencia.component';
import { DepositoComponent } from './deposito/deposito.component';

import { ConsultarExtratoComponent } from './consultar-extrato/consultar-extrato.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './guard/auth.guard';



const routes: Routes = [
  {path:'', redirectTo:'home', pathMatch:'full'},
  {path:'autocadastro', component: AutocadastroComponent },
  {path:'login', component:  LoginComponent},

  
  {path: 'saque', component: SaqueComponent, canActivate: [AuthGuard]},
  {path: 'transferencia', component: TransferenciaComponent, canActivate: [AuthGuard]},
  {path: 'deposito', component: DepositoComponent, canActivate: [AuthGuard]},

  {path: 'extrato', component: ConsultarExtratoComponent, canActivate: [AuthGuard]},
  {path: 'consultar-extrato', component: ConsultarExtratoComponent, canActivate: [AuthGuard]},

  {path: 'dashboard', component: DashboardComponent},
  {path: 'home', component: HomeComponent},
  
  //Administrador
  {path:'administrador', component: TelaInicialComponent, canActivate: [AuthGuard]},
  {path:'administrador/clientes', component: ClientesComponent, canActivate: [AuthGuard]},
  {path:'administrador/gerentes', component: ListarGerenteComponent, canActivate: [AuthGuard]},
  {path: 'administrador/gerentes/:id', component: InserirEditarGerenteComponent, canActivate: [AuthGuard]},

  //Gerentes
  {path:'gerente', component: TelaInicialGerenteComponent, canActivate: [AuthGuard]},
  {path:'gerente/consultar', component: ConsultarClienteComponent, canActivate: [AuthGuard]},
  {path:'gerente/consultar-todos', component: ConsultarTodosComponent, canActivate: [AuthGuard]},
  {path:'gerente/consultar-top3', component: ConsultarTop3Component, canActivate: [AuthGuard]},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
