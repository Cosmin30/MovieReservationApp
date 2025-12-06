import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { LoginFormComponent } from './presentation/components/login-form/login-form';
import { ProfileFormComponent } from './presentation/components/profile-form/profile-form';
import { RegisterFormComponent } from './presentation/components/register-form/register-form';

import { LoginPage } from './presentation/pages/login-page/login-page';
import { ProfilePage } from './presentation/pages/profile-page/profile-page';
import { RegisterPage } from './presentation/pages/register-page/register-page';

@NgModule({
  declarations: [],  
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    LoginFormComponent,
    ProfileFormComponent,
    RegisterFormComponent,
    LoginPage,
    ProfilePage,
    RegisterPage
  ]
})
export class UsersModule {}
