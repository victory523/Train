import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NotificationService } from './common-components/notification.service';
import { BackupService, LastBackup } from './services/backup.service';
import { WithingsService } from './services/withings.service';
import { RelativeTimePipe } from './utils/relative-time.pipe';
import { HttpRequestState, initialHttpRequestState, requestState } from './utils/request-state';
import { WeightComponent } from './weight/weight.component';
import { HeaderComponent } from './common-components/header/header.component';
import { HeaderMenuComponent } from './common-components/header-menu/header-menu.component';
import { MainComponent } from './common-components/main/main.component';
import { BadgeComponent } from './common-components/badge/badge.component';
import { LoaderComponent } from './common-components/loader/loader.component';
import { NotificationsComponent } from './common-components/notifications/notifications.component';
import { HeadingComponent } from './common-components/heading/heading.component';

@Component({
  standalone: true,
  imports:[
    NgIf,
    WeightComponent,
    RelativeTimePipe,
    HeadingComponent,
    HeaderComponent,
    HeaderMenuComponent,
    MainComponent,
    BadgeComponent,
    LoaderComponent,
    NotificationsComponent
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],

})
export class AppComponent implements OnInit {
  constructor(
    private withingsService: WithingsService,
    private notificationService: NotificationService,
    private backupService: BackupService
  ) {}

  syncState: HttpRequestState<void> = initialHttpRequestState;
  lastNackupState: HttpRequestState<LastBackup> = initialHttpRequestState;

  ngOnInit(): void {
    // setInterval(() => this.notificationService.showNotification('hello', 'error'), 1000);
    requestState(this.withingsService.sync(), (newState) => {
      this.syncState = newState;

      if (newState.hasFailed) {
        this.notificationService.showNotification(
          'Unable to sync with Withings',
          'error'
        );
      }
    });

    requestState(
      this.backupService.getLastBackupTime(),
      (newState) => {
        this.lastNackupState = newState;

        if (newState.isReady && newState.value.errorMessage) {
          this.notificationService.showNotification(
            newState.value.errorMessage,
            'error'
          );
        }

        if (newState.hasFailed) {
          this.notificationService.showNotification(
            'Unable to fetch last backup time',
            'error'
          );
        }
      }
    );
  }
}
