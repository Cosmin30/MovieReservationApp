import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.html',
  styleUrls: ['./register-form.css']
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
