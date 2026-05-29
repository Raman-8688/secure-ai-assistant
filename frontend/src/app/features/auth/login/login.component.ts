import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  email = '';
  password = '';

  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/chat']);
    }
  }

  // login(): void {
  //   this.errorMessage = '';

  //   const request: LoginRequest = {
  //     email: this.email.trim(),
  //     password: this.password
  //   };

  //   if (!request.email || !request.password) {
  //     this.errorMessage = 'Please enter email and password';
  //     return;
  //   }

  //   this.isLoading = true;

  //   this.authService.login(request).subscribe({
  //     next: (response) => {
  //       this.isLoading = false;

  //       if (!response.token) {
  //         this.errorMessage = response.message;
  //         return;
  //       }

  //       this.authService.saveToken(response.token);
  //       this.router.navigate(['/chat']);
  //     },
  //     error: (error) => {
  //       this.isLoading = false;
  //       this.errorMessage =
  //         error?.error?.message || error?.error || 'Login failed';
  //     }
  //   });
  // }
  login(): void {
  

    this.errorMessage = '';

    const request: LoginRequest = {
      email: this.email.trim(),
      password: this.password,
    };

    

    if (!request.email || !request.password) {
      this.errorMessage = 'Please enter email and password';
      // alert('3. email/password missing');
      return;
    }

    this.isLoading = true;

    // alert('4. calling backend login API');

    this.authService.login(request).subscribe({
      next: (response) => {
        // alert('5. backend response received');

        // alert('6. token value = ' + response.token);

        this.isLoading = false;

        if (!response.token) {
          // alert('7. token missing');

          this.errorMessage = response.message;
          return;
        }

        // SAVE TOKEN
        this.authService.saveToken(response.token);

        // alert('8. token save method called');

        const storedToken = localStorage.getItem('ai_auth_token');

        // alert('9. token from localStorage = ' + storedToken);

        // if (storedToken) {
        //   alert('10. token stored successfully');
        // } else {
        //   alert('11. token NOT stored');
        // }

        this.router.navigate(['/chat']);

        // alert('12. navigated to chat');
      },

      error: (error) => {
        alert('ERROR BLOCK CALLED');

        alert(JSON.stringify(error));

        this.isLoading = false;

        this.errorMessage =
          error?.error?.message || error?.error || 'Login failed';
      },
    });
  }
}
