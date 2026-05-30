// src/app/app.config.ts
// REPLACE your existing app.config.ts with this

import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([
        authInterceptor,   // Adds JWT token to every request
        errorInterceptor   // Converts all HTTP errors to user-friendly messages
      ])
    )
  ]
};