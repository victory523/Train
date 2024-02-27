import { Component, OnInit } from '@angular/core';
import { AuthService } from './auth.service';

@Component({
  standalone: true,
  selector: 'signin-redirect-callback',
  template: '',
})
export class SigninRedirectCallbackComponent implements OnInit {
  constructor(private readonly authService: AuthService) {}

  ngOnInit(): void {
    this.authService.handleSigninRedirectCallback();
  }
}
