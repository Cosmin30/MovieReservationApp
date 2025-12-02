import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginPage } from './presentation/pages/login-page/login-page';
import { ProfilePage } from './presentation/pages/profile-page/profile-page';
import { RegisterPage } from './presentation/pages/register-page/register-page';

const routes: Routes = [
  { path: 'login', component: LoginPage },
  { path: 'profile', component: ProfilePage },
  { path: 'register', component: RegisterPage }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule {}
