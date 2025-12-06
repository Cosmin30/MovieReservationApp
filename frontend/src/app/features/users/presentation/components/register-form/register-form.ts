import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';

export interface RegisterFormData {
  fullName: string;
  email: string;
  password: string;
}

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.html',
  styleUrls: ['./register-form.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule] 
})
export class RegisterFormComponent {
  @Output() onRegister = new EventEmitter<RegisterFormData>();

  form: RegisterFormData = {
    fullName: '',
    email: '',
    password: ''
  };

  submit(form: NgForm): void {
    if (form.valid) {
      // Sanitize input
      const sanitizedData: RegisterFormData = {
        fullName: this.form.fullName.trim(),
        email: this.form.email.trim().toLowerCase(),
        password: this.form.password
      };
      this.onRegister.emit(sanitizedData);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsTouched();
      });
    }
  }
}
