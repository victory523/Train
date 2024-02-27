import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { RouterTokens } from './app.routes';
import { BadgeComponent } from './common-components/badge/badge.component';
import { HeaderMenuComponent } from './common-components/header-menu/header-menu.component';
import { HeaderComponent } from './common-components/header/header.component';
import { HeadingComponent } from './common-components/heading/heading.component';
import { LoaderComponent } from './common-components/loader/loader.component';
import { MainComponent } from './common-components/main/main.component';
import { NotificationsComponent } from './common-components/notifications/notifications.component';
import { BackupService } from './backup/backup.service';
import { RelativeTimePipe } from './utils/relative-time.pipe';
import { AuthService } from './auth/auth.service';
import { combineLatest, map } from 'rxjs';
import { ButtonComponent } from './common-components/button/button.component';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    RelativeTimePipe,
    ButtonComponent,
    HeadingComponent,
    HeaderComponent,
    HeaderMenuComponent,
    MainComponent,
    BadgeComponent,
    LoaderComponent,
    NotificationsComponent,
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  readonly routerTokens = RouterTokens;
  readonly $lastBackupTime = this.backupService.getLastBackupTime();
  $isSignedIn = this.authService.isSignedIn();
  $userName = this.authService.getUserName();

  $vm = combineLatest([this.$isSignedIn, this.$userName]).pipe(
    map(([isSignedIn, userName]) => ({
      isSignedIn,
      userName,
    }))
  );

  constructor(
    private readonly authService: AuthService,
    private readonly backupService: BackupService
  ) {}

  onSignin(): void {
    this.authService.signin();
  }

  onSignout(): void {
    this.authService.signout();
  }
}
