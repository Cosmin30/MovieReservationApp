import { Component } from '@angular/core';
import { RegisterService } from '../../../application/use-cases/register-service';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.html',
  styleUrls: ['./register-page.css']
})
export class RegisterPage {

  constructor(private registerService: RegisterService) {}

  register(form: any) {
    this.registerService.execute(form).subscribe();
  }
}
