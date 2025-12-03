import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.html',
  styleUrls: ['./register-form.css'],
  standalone: true,
  imports: [CommonModule, FormsModule] 
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
