import { Injectable, inject } from '@angular/core';
import { AuthApiService } from '../../infrastructure/adapters/auth-api-service';
import { RegisterFormData } from '../../presentation/components/register-form/register-form';
import { RegisterRequestDTO } from '../../infrastructure/dtos/register-request.dto';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  private api = inject(AuthApiService);

  execute(form: RegisterFormData) {
    const dto: RegisterRequestDTO = {
      email: form.email,
      fullName: form.fullName,
      password: form.password
    };
    return this.api.register(dto);
  }
}
