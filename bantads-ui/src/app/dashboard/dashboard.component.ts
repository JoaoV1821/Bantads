import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  isVisible: boolean = true;
  saldo: string = '1000,00';

  public hideSaldo(): void {
    this.isVisible = !this.isVisible;
    
  }

}
