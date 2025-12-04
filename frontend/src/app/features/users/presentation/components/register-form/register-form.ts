import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.html',
  styleUrls: ['./register-form.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule] 
})
export class RegisterFormComponent {
  @Output() onRegister = new EventEmitter<any>();

form = {
  fullName: '',
  email: '',
  password: ''
};
  submit() {
    this.onRegister.emit(this.form);
  }
}
