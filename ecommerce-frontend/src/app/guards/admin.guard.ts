import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const authState = this.authService.getAuthState();

    if (!authState.isAuthenticated) {
      this.router.navigate(['/login']);
      return false;
    }

    const roles = authState.user?.roles || [];
    const isAdmin = roles.includes('ROLE_ADMIN');

    if (!isAdmin) {
      console.warn('❌ Acceso denegado: usuario no tiene rol ADMIN');
      this.router.navigate(['/products']);
      return false;
    }

    console.log('✅ Acceso permitido: usuario con rol ADMIN');
    return true;
  }
}
