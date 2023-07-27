import { Component, OnInit } from '@angular/core';
import { NotificationService } from './common-components/notification.service';
import { WithingsService } from './withings.service';
import { BackupService, LastBackup } from './backup.service';
import { HttpRequestState, initialHttpRequestState, requestState } from './utils/request-state';

@Component({
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
    // setInterval(() => this.notificationService.showNotification('hello'), 1000);
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
