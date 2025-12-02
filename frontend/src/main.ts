import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { App } from './app/app';
import 'zone.js'; // necesar pentru HttpClient

bootstrapApplication(App, {
  providers: [
    provideHttpClient(withFetch()) 
  ]
}).catch(err => console.error(err));
