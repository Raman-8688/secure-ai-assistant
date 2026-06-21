// src/app/features/auth/register/register.component.ts
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthLayoutComponent } from '../../../shared/components/auth-layout/auth-layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/auth.model';
import { environment } from '../../../../environments/environment';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatSnackBarModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    AuthLayoutComponent
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  hidePassword = true;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  animatedLines: string[] = [
    'smart_toy Real-time AI Responses',
    'mic Voice Input & Chat History',
    'security Secure JWT Authentication',
    'public OAuth2 Login — Google & GitHub',
    'bolt Built with Angular & Spring Boot',
    'devices Fully Responsive Design'
  ];

  features: string[] = [
    'Secure JWT Authentication',
    'Email Verification',
    'OAuth2 with Google & GitHub',
    'Real-time AI Responses'
  ];

  get isDark(): boolean {
    return this.themeService.isDark;
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private themeService: ThemeService
  ) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/chat']);
    }
  }

  register(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const request: RegisterRequest = {
      name: this.name.trim(),
      email: this.email.trim(),
      password: this.password,
    };

    if (!request.name) {
      this.errorMessage = 'Please enter your name';
      return;
    }

    if (!request.email) {
      this.errorMessage = 'Please enter your email address';
      return;
    }

    if (!this.isValidEmail(request.email)) {
      this.errorMessage = 'Please enter a valid email address';
      return;
    }

    if (!request.password) {
      this.errorMessage = 'Please enter a password';
      return;
    }

    if (request.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      return;
    }

    this.isLoading = true;

    this.authService.register(request).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.successMessage = response.message || 'Registration successful!';
        
        this.snackBar.open('Registration successful! Check your email for OTP.', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });

        this.router.navigate(['/verify-email'], {
          queryParams: { email: request.email },
        });
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.userMessage || 'Registration failed. Please try again.';
        
        this.snackBar.open(this.errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      },
    });
  }

  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  signupWithGoogle(): void {
    window.location.href = `${environment.apiUrl}/oauth2/authorization/google`;
  }

  signupWithGitHub(): void {
    window.location.href = `${environment.apiUrl}/oauth2/authorization/github`;
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }
}