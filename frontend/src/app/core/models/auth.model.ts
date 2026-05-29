/*
 * These interfaces represent request and response data
 * between Angular frontend and Spring Boot backend.
 */

/*
 * Register form data.
 * This is sent to:
 * POST /api/auth/register
 */
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

/*
 * Login form data.
 * This is sent to:
 * POST /api/auth/login
 */
export interface LoginRequest {
  email: string;
  password: string;
}

/*
 * OTP verification data.
 * This is sent to:
 * POST /api/auth/verify-email
 */
export interface VerifyEmailRequest {
  email: string;
  otp: string;
}

/*
 * Resend OTP request.
 * This is sent to:
 * POST /api/auth/resend-otp
 */
export interface ResendOtpRequest {
  email: string;
}

/*
 * Login response from backend.
 * Backend returns JWT token after successful login.
 */
export interface AuthResponse {
  token: string | null;
  message: string;
}