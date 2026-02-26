import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-products-maintenance',
  standalone: true,
  imports: [CommonModule, CardModule, ButtonModule],
  templateUrl: './products-maintenance.component.html',
  styleUrl: './products-maintenance.component.scss'
})
export class ProductsMaintenanceComponent {
  constructor(private router: Router) {}

  goToAddProduct(): void {
    this.router.navigate(['/admin/products/add']);
  }
}
