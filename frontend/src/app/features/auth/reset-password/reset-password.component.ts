// src/app/components/auth/reset-password/reset-password.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
  selector: 'app-reset-password',
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
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  resetForm: FormGroup;
  token: string = '';
  isValidating = true;
  isTokenValid = false;
  isLoading = false;
  resetSuccess = false;
  hidePassword = true;
  hideConfirmPassword = true;

  get isDark(): boolean {
    return this.themeService.isDark;
  }

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private themeService: ThemeService
  ) {
    this.resetForm = this.fb.group({
      newPassword: ['', [
        Validators.required,
        Validators.minLength(8),
        this.passwordStrengthValidator
      ]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      if (this.token) {
        this.validateToken();
      } else {
        this.isValidating = false;
        this.snackBar.open('Invalid reset link. Please request a new one.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private validateToken(): void {
    this.authService.validateResetToken(this.token).subscribe({
      next: (response) => {
        this.isValidating = false;
        if (response.success) {
          this.isTokenValid = true;
        } else {
          this.snackBar.open(response.message, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      },
      error: (error) => {
        this.isValidating = false;
        const message = error.error?.message || 'Invalid or expired token.';
        this.snackBar.open(message, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onSubmit(): void {
    if (this.resetForm.invalid) {
      this.resetForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.authService.resetPassword({
      token: this.token,
      newPassword: this.resetForm.value.newPassword,
      confirmPassword: this.resetForm.value.confirmPassword
    }).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.resetSuccess = true;
          this.snackBar.open(response.message, 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        } else {
          this.snackBar.open(response.message, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      },
      error: (error) => {
        this.isLoading = false;
        const message = error.error?.message || 'Failed to reset password. Please try again.';
        this.snackBar.open(message, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.value;
    if (!password) return null;
    
    const hasLowerCase = /[a-z]/.test(password);
    const hasUpperCase = /[A-Z]/.test(password);
    const hasNumber = /\d/.test(password);
    const hasSpecialChar = /[@$!%*?&]/.test(password);
    
    const isValid = hasLowerCase && hasUpperCase && hasNumber && hasSpecialChar;
    
    return isValid ? null : { weakPassword: true };
  }

  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('newPassword')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { mismatch: true };
  }

  // Helper methods for template
  getPasswordValue(): string {
    return this.resetForm.get('newPassword')?.value || '';
  }

  hasMinLength(): boolean {
    return this.getPasswordValue().length >= 8;
  }

  hasLowerCase(): boolean {
    return /[a-z]/.test(this.getPasswordValue());
  }

  hasUpperCase(): boolean {
    return /[A-Z]/.test(this.getPasswordValue());
  }

  hasNumber(): boolean {
    return /\d/.test(this.getPasswordValue());
  }

  hasSpecialChar(): boolean {
    return /[@$!%*?&]/.test(this.getPasswordValue());
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }
}