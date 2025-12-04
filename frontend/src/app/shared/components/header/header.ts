import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/auth-service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule, AsyncPipe],
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})
export class HeaderComponent {

  authService = inject(AuthService);

  get isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  logout() {
    this.authService.logout();
  }
}
