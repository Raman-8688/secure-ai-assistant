// src/app/shared/components/auth-layout/auth-layout.component.ts
import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule, MatButtonModule, MatTooltipModule,MatProgressSpinnerModule],
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.css']
})
export class AuthLayoutComponent implements OnInit, OnDestroy {
  @Input() title: string = '';
  @Input() subtitle: string = '';
  @Input() formTitle: string = '';
  @Input() formSubtitle: string = '';
  @Input() icon: string = 'lock';
  @Input() showOAuth: boolean = true;
  @Input() showFeatures: boolean = false;
  @Input() isDark: boolean = false;
  @Input() isLoading: boolean = false;
  @Input() errorMessage: string = '';
  @Input() successMessage: string = '';
  @Input() animatedLines: string[] = [];
  @Input() features: string[] = [];
  @Input() formFirst: boolean = false; // NEW: false = OAuth first, true = Form first

  @Output() themeToggle = new EventEmitter<void>();
  @Output() googleLogin = new EventEmitter<void>();
  @Output() githubLogin = new EventEmitter<void>();

  currentLineIndex = 0;
  previousLineIndex = -1;
  private intervalId: any;

  get showSuccess(): boolean {
    return !!this.successMessage;
  }

  get showLoading(): boolean {
    return this.isLoading && !this.showSuccess;
  }

  ngOnInit(): void {
    if (this.animatedLines.length > 0) {
      this.startAnimation();
    }
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

  onThemeToggle(): void {
    this.themeToggle.emit();
  }

  onGoogleLogin(): void {
    this.googleLogin.emit();
  }

  onGithubLogin(): void {
    this.githubLogin.emit();
  }
}