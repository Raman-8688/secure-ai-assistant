import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';

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
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatTooltipModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
  email = '';
  password = '';
  isLoading = false;
  errorMessage = '';
  hidePassword = true;

  // ── Animated Text Lines (Using Material Icons) ──────────
  animatedLines: string[] = [
    'smart_toy Real-time AI Responses with Hugging Face',
    'mic Voice Input & Chat History',
    'security Secure JWT Authentication',
    'public OAuth2 Login — Google & GitHub',
    'bolt Built with Angular & Spring Boot',
    'devices Fully Responsive Design',
    'storage PostgreSQL on Neon Cloud Database',
    'verified Enterprise-grade Security'
  ];

  currentLineIndex = 0;
  previousLineIndex = -1;
  private intervalId: any;

  get isDark(): boolean {
    return this.themeService.isDark;
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private themeService: ThemeService,
  ) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/chat']);
    }
  }

  ngOnInit(): void {
    this.startAnimation();
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  private startAnimation(): void {
    this.intervalId = setInterval(() => {
      this.previousLineIndex = this.currentLineIndex;
      this.currentLineIndex = (this.currentLineIndex + 1) % this.animatedLines.length;
    }, 2200);
  }

  // ── Auth Methods ──────────────────────────────────────────
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
          this.errorMessage = response.message;
          return;
        }
        this.authService.saveToken(response.token);
        this.router.navigate(['/chat']);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error?.error?.message || 'Login failed. Please try again.';
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