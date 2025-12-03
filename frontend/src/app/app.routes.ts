import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { MoviesListPage } from './features/movies/presentation/pages/movies-list-page/movies-list-page';
import { MovieDetailsPage } from './features/movies/presentation/pages/movie-details-page/movie-details-page';
import { ScreeningListComponent } from './features/screenings/presentation/components/screening-list/screening-list';
import { ScreeningCardComponent } from './features/screenings/presentation/components/screening-card/screening-card';
import { TicketCardComponent } from './features/tickets/presentation/components/ticket-card/ticket-card';
import { HallLayoutComponent } from './features/halls/presentation/components/hall-layout/hall-layout';
import { HallsListComponent } from './features/halls/presentation/components/halls-list/halls-list';
import { LoginPage } from './features/users/presentation/pages/login-page/login-page';
import { RegisterPage } from './features/users/presentation/pages/register-page/register-page';
import { ProfilePage } from './features/users/presentation/pages/profile-page/profile-page';
import { MyReservationsPage } from './features/reservations/presentation/pages/my-reservations-page/my-reservations-page';
import { ReservationPage } from './features/reservations/presentation/pages/reservation-page/reservation-page';
import { authGuard } from './core/guards/auth-guard';
import { guestGuard } from './core/guards/guest-guard';


export const routes: Routes = [

   { 
    path: 'login', 
    component: LoginPage,
    canActivate: [guestGuard]
  },
  { 
    path: 'register', 
    component: RegisterPage,
    canActivate: [guestGuard]
  },
  
  // Rute protejate (doar pentru utilizatori autentificați)
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {path:'home',component:HomeComponent,canActivate:[authGuard]},
  { path: 'movies', component: MoviesListPage, canActivate: [authGuard] },
  { path: 'movies/:id', component: MovieDetailsPage, canActivate: [authGuard] },
  { path: 'screenings', component: ScreeningListComponent, canActivate: [authGuard] },
  { path: 'screenings/:id', component: ScreeningCardComponent, canActivate: [authGuard] },
  { path: 'tickets/:id', component: TicketCardComponent, canActivate: [authGuard] },
  { path: 'halls', component: HallsListComponent, canActivate: [authGuard] },
  { path: 'halls/:id', component: HallLayoutComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfilePage, canActivate: [authGuard] },
  { path: 'reservations', component: MyReservationsPage, canActivate: [authGuard] },
  { path: 'reservations/new', component: ReservationPage, canActivate: [authGuard] },
  { path: 'reservations/:id', component: ReservationPage, canActivate: [authGuard] },
  
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
