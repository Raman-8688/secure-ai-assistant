import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.model';
import { environment } from '../../../../environments/environment';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
  email = '';
  password = '';
  isLoading = false;
  errorMessage = '';

  // ── Animated lines ─────────────────────────────────────────
  // All lines that will cycle through
  private readonly ALL_LINES = [
    'Built with Java 17 and Spring Boot 3 — production-ready backend.',
    'OAuth2 login via Google and GitHub with Spring Security.',
    'JWT tokens issued and validated on every secure request.',
    'Angular 17 standalone components with lazy-loaded routes.',
    'PostgreSQL on Neon — cloud-native relational database.',
    'JPA / Hibernate ORM with proper entity relationships.',
    'Full chat history persisted per user via ChatHistory entity.',
    'Real-time AI responses powered by the Hugging Face API.',
    'Global exception handler for clean, structured error responses.',
    'Angular error interceptor with user-friendly toast messages.',
    'NetworkService detects offline state before every API call.',
    'Multi-stage Dockerfile — backend deployed on Render.',
    'Angular frontend deployed on Vercel with CI/CD pipeline.',
    'Route guards protect /chat from unauthenticated access.',
    'Auth interceptor injects JWT Bearer token automatically.',
    'Markdown renderer in chat — code blocks, lists, bold text.',
    'Environment-based config for local and production builds.',
    'Reactive forms with real-time validation and error display.',
    'Responsive layout — works on desktop, tablet, and mobile.',
    'Entire project engineered solo by Raman — Hyderabad.',
  ];

  private readonly MAX_VISIBLE = 7;   // lines shown at once
  private readonly INTERVAL_MS = 1800; // new line every 1.8s

  visibleLines: string[] = [];
  fadingLines = new Set<number>();

  private lineIndex = 0;
  private timer: any;

  // ── Theme ──────────────────────────────────────────────────
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
    // Seed first N lines instantly
    this.visibleLines = this.ALL_LINES.slice(0, this.MAX_VISIBLE);
    this.lineIndex = this.MAX_VISIBLE % this.ALL_LINES.length;
    this.startAnimation();
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }

  private startAnimation(): void {
    this.timer = setInterval(() => {
      // Mark oldest line as fading
      this.fadingLines.add(0);

      setTimeout(() => {
        // Remove the oldest line
        this.visibleLines.shift();
        this.fadingLines.clear();

        // Append next line
        this.visibleLines.push(this.ALL_LINES[this.lineIndex]);
        this.lineIndex = (this.lineIndex + 1) % this.ALL_LINES.length;
      }, 380); // matches CSS fade duration

    }, this.INTERVAL_MS);
  }

  // ── Auth ───────────────────────────────────────────────────
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
        this.errorMessage =
          error?.error?.message || error?.error || 'Login failed. Please try again.';
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