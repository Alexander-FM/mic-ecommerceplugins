import { Routes } from '@angular/router';
import { ProductsComponent } from './pages/products/products.component';
import { ProductDetailComponent } from './pages/product-detail/product-detail.component';
import { CartComponent } from './pages/cart/cart.component';
import { MyOrdersComponent } from './pages/my-orders/my-orders.component';
import { OrderDetailComponent } from './pages/order-detail/order-detail.component';
import { LoginComponent } from './pages/login/login.component';
import { AuthCallbackComponent } from './pages/auth-callback/auth-callback.component';
import { RegisterComponent } from './pages/register/register.component';
import { AddProductComponent } from './pages/admin/add-product/add-product.component';
import { ProductsMaintenanceComponent } from './pages/admin/products-maintenance/products-maintenance.component';
import { CategoriesMaintenanceComponent } from './pages/admin/categories-maintenance/categories-maintenance.component';
import { BrandsMaintenanceComponent } from './pages/admin/brands-maintenance/brands-maintenance.component';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/products', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'auth/callback', component: AuthCallbackComponent },
  { path: 'products', component: ProductsComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'cart', component: CartComponent },
  { path: 'my-orders', component: MyOrdersComponent, canActivate: [AuthGuard] },
  { path: 'orders/:id', component: OrderDetailComponent, canActivate: [AuthGuard] },
  { path: 'admin/maintenance/products', component: ProductsMaintenanceComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/maintenance/categories', component: CategoriesMaintenanceComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/maintenance/brands', component: BrandsMaintenanceComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/products/add', component: AddProductComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/products/edit/:id', component: AddProductComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: '**', redirectTo: '/products' }
];
