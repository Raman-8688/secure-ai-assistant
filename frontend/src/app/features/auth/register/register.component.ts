import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/auth.model';



@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';

  isLoading = false;
  message = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  register(): void {
    this.message = '';
    this.errorMessage = '';

    const request: RegisterRequest = {
      name: this.name.trim(),
      email: this.email.trim(),
      password: this.password
    };

    if (!request.name || !request.email || !request.password) {
      this.errorMessage = 'Please fill all fields';
      return;
    }

    this.isLoading = true;

    this.authService.register(request).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.message = response;

        /*
         * After successful registration, move user to OTP verification page.
         * We pass email in query params so verify page can auto-fill email.
         */
        this.router.navigate(['/verify-email'], {
          queryParams: { email: request.email }
        });
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage =
          error?.error?.message || error?.error || 'Registration failed';
      }
    });
  }
}