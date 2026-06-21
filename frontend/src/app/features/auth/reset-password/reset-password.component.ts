// src/app/features/auth/reset-password/reset-password.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthLayoutComponent } from '../../../shared/components/auth-layout/auth-layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-reset-password',
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
  errorMessage = '';
  successMessage = '';

  animatedLines: string[] = [
    'lock Create a new strong password',
    'security Password must be secure',
    'verified Confirm your new password',
    'check_circle Password reset complete'
  ];

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
        this.errorMessage = 'Invalid reset link. Please request a new one.';
        this.snackBar.open(this.errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private validateToken(): void {
    this.authService.validateResetToken(this.token).subscribe({
      next: (response: any) => {
        this.isValidating = false;
        if (response.message?.includes('valid') || response.status === 'success') {
          this.isTokenValid = true;
        } else {
          this.errorMessage = response.message || 'Invalid or expired token.';
          this.snackBar.open(this.errorMessage, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      },
      error: (error) => {
        this.isValidating = false;
        this.errorMessage = error.userMessage || 'Invalid or expired token.';
        this.snackBar.open(this.errorMessage, 'Close', {
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
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.resetPassword({
      token: this.token,
      newPassword: this.resetForm.value.newPassword,
      confirmPassword: this.resetForm.value.confirmPassword
    }).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        if (response.message?.includes('successful') || response.success) {
          this.resetSuccess = true;
          this.successMessage = response.message || 'Password reset successful!';
          
          this.snackBar.open(this.successMessage, 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        } else {
          this.errorMessage = response.message || 'Failed to reset password.';
          this.snackBar.open(this.errorMessage, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.userMessage || 'Failed to reset password. Please try again.';
        this.snackBar.open(this.errorMessage, 'Close', {
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