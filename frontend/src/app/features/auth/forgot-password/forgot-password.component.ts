// src/app/components/auth/forgot-password/forgot-password.component.ts
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  isLoading = false;
  isSubmitted = false;

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

    this.authService.forgotPassword({
      email: this.forgotForm.value.email.toLowerCase().trim()
    }).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.isSubmitted = true;
        this.snackBar.open(response.message, 'Close', {
          duration: 5000,
          panelClass: ['success-snackbar']
        });
      },
      error: (error) => {
        this.isLoading = false;
        
        let message = 'Failed to send reset link. Please try again.';
        
        if (error.status === 404) {
          message = 'This email is not registered. Please create an account first.';
        } else if (error.status === 429) {
          message = 'Too many reset attempts. Please try again after 24 hours.';
        } else if (error.error?.message) {
          message = error.error.message;
        }
        
        this.snackBar.open(message, 'Close', {
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