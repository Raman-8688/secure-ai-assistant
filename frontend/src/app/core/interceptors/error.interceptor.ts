// src/app/core/interceptors/error.interceptor.ts
// REPLACE your existing error.interceptor.ts with this

import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NetworkService } from '../services/network.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const networkService = inject(NetworkService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 1. No internet connection
      if (!networkService.isOnline) {
        return throwError(() => ({
          userMessage:
            'No internet connection. Please check your network and try again.',
          type: 'network',
        }));
      }

      // 2. Network-level failure (backend unreachable)
      if (error.status === 0) {
        return throwError(() => ({
          userMessage:
            'Unable to connect to the server. Please try again in a moment.',
          type: 'server_down',
        }));
      }

      // 3. Auth errors
      if (error.status === 401) {
        return throwError(() => ({
          userMessage: 'Your session has expired. Please log in again.',
          type: 'auth',
        }));
      }

      if (error.status === 403) {
        return throwError(() => ({
          userMessage: 'You do not have permission to perform this action.',
          type: 'forbidden',
        }));
      }

      // 4. Backend validation errors
      if (error.status === 400) {
        const msg =
          error.error?.error ||
          error.error?.message ||
          'Invalid request. Please check your input.';
        return throwError(() => ({
          userMessage: msg,
          type: 'validation',
        }));
      }

      // 5. Backend/AI service error
      if (error.status === 500 || error.status === 503) {
        const msg =
          error.error?.error ||
          'The AI service is temporarily unavailable. Please try again later.';
        return throwError(() => ({
          userMessage: msg,
          type: 'server_error',
        }));
      }

      // 6. Fallback — never show raw error details
      return throwError(() => ({
        userMessage: 'Something went wrong. Please try again.',
        type: 'unknown',
      }));
    }),
  );
};
