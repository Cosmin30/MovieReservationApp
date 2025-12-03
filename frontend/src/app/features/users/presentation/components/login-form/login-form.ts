import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router'; 

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

  @Output() onLogin = new EventEmitter<any>();

  form = {
    email: '',
    password: ''
  };

  submit() {
    this.onLogin.emit(this.form);
  }
}
