import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.html',
  styleUrls: ['./login-form.css']
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
