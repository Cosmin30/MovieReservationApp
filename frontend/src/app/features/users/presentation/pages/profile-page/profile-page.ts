import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../../core/auth/auth-service';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.css'],
  standalone: true,
  imports: [CommonModule, RouterLink]
})
export class ProfilePage implements OnInit {
  authService = inject(AuthService);
  profile: any = null;

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        // Normalize createdAt - handle both snake_case and camelCase, and null values
        this.profile = {
          ...user,
          createdAt: user.createdAt || (user as any).created_at || null
        };
      }
    });
  }
}
