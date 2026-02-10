import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MessageModule } from 'primeng/message';
import { LoginComponent } from '../login/login.component';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [CommonModule, MessageModule, LoginComponent],
  template: `<app-login></app-login>`
})
export class AuthCallbackComponent {}
