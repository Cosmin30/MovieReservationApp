import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { App } from './app/app';
import { routes } from './app/app.routes';
import { authInterceptor } from './app/core/interceptors/auth-interceptor';
import 'zone.js'; // obligatoriu pentru Angular cu Zone.js

bootstrapApplication(App, {
  providers: [
    // HTTP client cu interceptori din DI
    provideHttpClient(withInterceptorsFromDi()),
    // router
    provideRouter(routes),
    // interceptorul nostru de autentificare
    {
      provide: 'HTTP_INTERCEPTORS',
      useValue: authInterceptor,
      multi: true
    }
  ]
}).catch(err => console.error(err));
