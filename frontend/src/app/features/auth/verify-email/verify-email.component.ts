// src/app/features/auth/verify-email/verify-email.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthLayoutComponent } from '../../../shared/components/auth-layout/auth-layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { VerifyEmailRequest } from '../../../core/models/auth.model';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatSnackBarModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    AuthLayoutComponent
  ],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {
  email = '';
  otp = '';
  isLoading = false;
  isResending = false;
  errorMessage = '';
  successMessage = '';
  isVerified = false;

  animatedLines: string[] = [
    'verified Verify your email to get started',
    'email Check your inbox for the OTP',
    'security Secure two-factor verification',
    'rocket_launch Unlock full access'
  ];

  get isDark(): boolean {
    return this.themeService.isDark;
  }

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.email = this.route.snapshot.queryParamMap.get('email') || '';
    
    if (!this.email) {
      this.router.navigate(['/register']);
    }
  }

  verifyEmail(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const request: VerifyEmailRequest = {
      email: this.email.trim(),
      otp: this.otp.trim()
    };

    if (!request.otp) {
      this.errorMessage = 'Please enter the OTP';
      return;
    }

    if (request.otp.length !== 6) {
      this.errorMessage = 'OTP must be 6 digits';
      return;
    }

    this.isLoading = true;

    this.authService.verifyEmail(request).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.isVerified = true;
        this.successMessage = response.message || 'Email verified successfully!';
        
        this.snackBar.open(this.successMessage, 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.userMessage || 'OTP verification failed. Please try again.';
        
        this.snackBar.open(this.errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  resendOtp(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.email.trim()) {
      this.errorMessage = 'Email is required';
      return;
    }

    this.isResending = true;

    this.authService.resendOtp({ email: this.email.trim() }).subscribe({
      next: (response: any) => {
        this.isResending = false;
        this.successMessage = response.message || 'OTP resent successfully!';
        this.otp = '';
        
        this.snackBar.open(this.successMessage, 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: (error) => {
        this.isResending = false;
        this.errorMessage = error.userMessage || 'Failed to resend OTP';
        
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

  toggleTheme(): void {
    this.themeService.toggle();
  }
}