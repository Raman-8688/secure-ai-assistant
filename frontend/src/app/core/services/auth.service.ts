import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  RegisterRequest,
  VerifyEmailRequest,
  LoginRequest,
  AuthResponse,
  ResendOtpRequest,
} from '../models/auth.model';

/*
 * AuthService handles all authentication API calls.
 *
 * It talks to Spring Boot backend:
 * - register
 * - verify email
 * - login
 * - resend OTP
 *
 * It also stores and reads JWT token from localStorage.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  /*
   * Base URL for backend authentication APIs.
   */
  private readonly authApiUrl = 'http://localhost:8080/api/auth';

  /*
   * Token key used in localStorage.
   */
  private readonly tokenKey = 'ai_auth_token';

  constructor(private http: HttpClient) {}

  /*
   * Register new user.
   *
   * Backend sends OTP to email after successful registration.
   */
  register(request: RegisterRequest): Observable<string> {
    return this.http.post(`${this.authApiUrl}/register`, request, {
      responseType: 'text',
    });
  }

  /*
   * Verify email using OTP.
   */
  verifyEmail(request: VerifyEmailRequest): Observable<string> {
    return this.http.post(`${this.authApiUrl}/verify-email`, request, {
      responseType: 'text',
    });
  }

  /*
   * Login user.
   *
   * Backend returns JWT token.
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authApiUrl}/login`, request);
  }

  /*
   * Resend OTP to email.
   */
  resendOtp(request: ResendOtpRequest): Observable<string> {
    return this.http.post(`${this.authApiUrl}/resend-otp`, request, {
      responseType: 'text',
    });
  }

  /*
   * Save JWT token after login.
   */
  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  /*
   * Read JWT token.
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /*
   * Check user logged in or not.
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /*
   * Logout user.
   */
  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }
}
