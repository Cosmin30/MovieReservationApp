import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';

export interface LoginFormData {
  email: string;
  password: string;
}

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.html',
  styleUrls: ['./login-form.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule   
  ]
})
export class LoginFormComponent {
  @Output() onLogin = new EventEmitter<LoginFormData>();

  form: LoginFormData = {
    email: '',
    password: ''
  };

  submit(form: NgForm): void {
    if (form.valid) {
      // Sanitize email (trim whitespace, lowercase)
      const sanitizedData: LoginFormData = {
        email: this.form.email.trim().toLowerCase(),
        password: this.form.password
      };
      this.onLogin.emit(sanitizedData);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });
    }
  }
}
