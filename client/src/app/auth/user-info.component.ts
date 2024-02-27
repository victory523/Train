import { Component } from '@angular/core';
import { AuthService } from './auth.service';
import { combineLatest, map } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { ButtonComponent } from '../common-components/button/button.component';

@Component({
  standalone: true,
  selector: 'user-info',
  imports: [AsyncPipe, HeadingComponent, ButtonComponent],
  templateUrl: './user-info.component.html',
  styleUrl: './user-info.component.css',
})
export class UserInfoComponent {
  $isSignedIn = this.authService.isSignedIn();
  $userName = this.authService.getUserName();

  $vm = combineLatest([this.$isSignedIn, this.$userName]).pipe(
    map(([isSignedIn, userName]) => ({
      isSignedIn,
      userName,
    }))
  );

  constructor(private readonly authService: AuthService) {}

  onSignin(): void {
    this.authService.signin();
  }

  onSignout(): void {
    this.authService.signout();
  }
}
