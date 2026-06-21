// src/app/features/auth/login/login.component.ts
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
import { LoginRequest } from '../../../core/models/auth.model';
import { environment } from '../../../../environments/environment';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-login',
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
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = '';
  password = '';
  hidePassword = true;
  isLoading = false;
  errorMessage = '';

  animatedLines: string[] = [
    'smart_toy Real-time AI Responses',
    'mic Voice Input & Chat History',
    'security Secure JWT Authentication',
    'public OAuth2 Login — Google & GitHub',
    'bolt Built with Angular & Spring Boot',
    'devices Fully Responsive Design',
    'storage PostgreSQL on Neon Cloud Database',
    'verified Enterprise-grade Security'
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

  login(): void {
    this.errorMessage = '';

    const request: LoginRequest = {
      email: this.email.trim(),
      password: this.password,
    };

    if (!request.email || !request.password) {
      this.errorMessage = 'Please enter your email and password.';
      return;
    }

    this.isLoading = true;

    this.authService.login(request).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (!response.token) {
          this.errorMessage = response.message || 'Login failed. Please try again.';
          this.snackBar.open(this.errorMessage, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
          return;
        }
        this.authService.saveToken(response.token);
        this.router.navigate(['/chat']);
      },
      error: (error) => {
        this.isLoading = false;
        // ✅ Use the error message from the interceptor (which now gets backend message)
        this.errorMessage = error.userMessage || 'Login failed. Please try again.';
        
        this.snackBar.open(this.errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      },
    });
  }

  loginWithGoogle(): void {
    window.location.href = `${environment.apiUrl}/oauth2/authorization/google`;
  }

  loginWithGitHub(): void {
    window.location.href = `${environment.apiUrl}/oauth2/authorization/github`;
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }
}