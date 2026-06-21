// src/app/features/auth/forgot-password/forgot-password.component.ts
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthLayoutComponent } from '../../../shared/components/auth-layout/auth-layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    AuthLayoutComponent
  ],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  isLoading = false;
  isSubmitted = false;
  errorMessage = '';
  successMessage = '';

  animatedLines: string[] = [
    'security Reset your password securely',
    'email Check your email for reset link',
    'lock_reset Create a new strong password',
    'verified Enterprise-grade security'
  ];

  get isDark(): boolean {
    return this.themeService.isDark;
  }

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private themeService: ThemeService
  ) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.forgotForm.invalid) {
      this.forgotForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.forgotPassword({
      email: this.forgotForm.value.email.toLowerCase().trim()
    }).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.isSubmitted = true;
        this.successMessage = response.message;
        
        this.snackBar.open(response.message, 'Close', {
          duration: 5000,
          panelClass: ['success-snackbar']
        });
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.userMessage || 'Failed to send reset link. Please try again.';
        
        this.snackBar.open(this.errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }
}