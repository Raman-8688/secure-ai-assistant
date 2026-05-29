import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { VerifyEmailRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {
  email = '';
  otp = '';

  isLoading = false;
  isResending = false;

  message = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.email = this.route.snapshot.queryParamMap.get('email') || '';
  }

  verifyEmail(): void {
    this.message = '';
    this.errorMessage = '';

    const request: VerifyEmailRequest = {
      email: this.email.trim(),
      otp: this.otp.trim()
    };

    if (!request.email || !request.otp) {
      this.errorMessage = 'Please enter email and OTP';
      return;
    }

    this.isLoading = true;

    this.authService.verifyEmail(request).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.message = response;

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1200);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error?.error?.message || error?.error || 'OTP verification failed';
      }
    });
  }

  resendOtp(): void {
    this.message = '';
    this.errorMessage = '';

    if (!this.email.trim()) {
      this.errorMessage = 'Please enter email first';
      return;
    }

    this.isResending = true;

    this.authService.resendOtp({ email: this.email.trim() }).subscribe({
      next: (response) => {
        this.isResending = false;
        this.message = response;
      },
      error: (error) => {
        this.isResending = false;
        this.errorMessage =
          error?.error?.message || error?.error || 'Failed to resend OTP';
      }
    });
  }
}