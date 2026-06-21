// src/app/core/interceptors/error.interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NetworkService } from '../services/network.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const networkService = inject(NetworkService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // No internet connection
      if (!networkService.isOnline) {
        return throwError(() => ({
          userMessage: 'No internet connection. Please check your network and try again.',
          type: 'network',
        }));
      }

      // Network-level failure
      if (error.status === 0) {
        return throwError(() => ({
          userMessage: 'Unable to connect to the server. Please try again in a moment.',
          type: 'server_down',
        }));
      }

      // ✅ Check if it's a login or register request
      const isAuthRequest = req.url.includes('/api/auth/login') || 
                           req.url.includes('/api/auth/register') ||
                           req.url.includes('/api/auth/verify-email') ||
                           req.url.includes('/api/auth/resend-otp') ||
                           req.url.includes('/api/auth/forgot-password') ||
                           req.url.includes('/api/auth/reset-password');

      // 401 - Unauthorized
      if (error.status === 401) {
        // ✅ For login/register endpoints, use backend message
        if (isAuthRequest) {
          let msg = 'Invalid credentials. Please try again.';
          if (error.error?.error) {
            msg = error.error.error;
          } else if (error.error?.message) {
            msg = error.error.message;
          }
          return throwError(() => ({
            userMessage: msg,
            type: 'auth_error',
          }));
        }
        
        // For protected endpoints, session expired
        return throwError(() => ({
          userMessage: 'Your session has expired. Please log in again.',
          type: 'auth',
        }));
      }

      // 403 - Forbidden
      if (error.status === 403) {
        let msg = 'You do not have permission to perform this action.';
        if (error.error?.error) {
          msg = error.error.error;
        } else if (error.error?.message) {
          msg = error.error.message;
        }
        return throwError(() => ({
          userMessage: msg,
          type: 'forbidden',
        }));
      }

      // 404 - Not Found
      if (error.status === 404) {
        let msg = 'Resource not found.';
        if (error.error?.error) {
          msg = error.error.error;
        } else if (error.error?.message) {
          msg = error.error.message;
        }
        return throwError(() => ({
          userMessage: msg,
          type: 'not_found',
        }));
      }

      // 400 - Bad Request (Validation errors)
      if (error.status === 400) {
        let msg = 'Invalid request. Please check your input.';
        if (error.error?.error) {
          msg = error.error.error;
        } else if (error.error?.message) {
          msg = error.error.message;
        } else if (typeof error.error === 'string') {
          msg = error.error;
        }
        return throwError(() => ({
          userMessage: msg,
          type: 'validation',
        }));
      }

      // 429 - Too Many Requests
      if (error.status === 429) {
        let msg = 'Too many requests. Please try again later.';
        if (error.error?.error) {
          msg = error.error.error;
        } else if (error.error?.message) {
          msg = error.error.message;
        }
        return throwError(() => ({
          userMessage: msg,
          type: 'rate_limit',
        }));
      }

      // 500 - Server Error
      if (error.status === 500 || error.status === 503) {
        let msg = 'The service is temporarily unavailable. Please try again later.';
        if (error.error?.error) {
          msg = error.error.error;
        } else if (error.error?.message) {
          msg = error.error.message;
        }
        return throwError(() => ({
          userMessage: msg,
          type: 'server_error',
        }));
      }

      // Fallback - try to get message from backend
      let fallbackMsg = 'Something went wrong. Please try again.';
      if (error.error?.error) {
        fallbackMsg = error.error.error;
      } else if (error.error?.message) {
        fallbackMsg = error.error.message;
      } else if (typeof error.error === 'string') {
        fallbackMsg = error.error;
      }

      return throwError(() => ({
        userMessage: fallbackMsg,
        type: 'unknown',
      }));
    }),
  );
};