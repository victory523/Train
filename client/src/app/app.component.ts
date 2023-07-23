import { Component, OnInit } from '@angular/core';
import { NotificationService } from './common-components/notification.service';
import { HttpRequestState } from './types';
import { initialHttpRequestState, subscribeToRequestState } from './utils';
import { WithingsService } from './withings.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(
    private withingsService: WithingsService,
    private notificationService: NotificationService
  ) {}

  syncState: HttpRequestState<void> = initialHttpRequestState;

  ngOnInit(): void {
    // setInterval(() => this.notificationService.showNotification('hello'), 1000);
    subscribeToRequestState(this.withingsService.sync(), (newState) => {
      this.syncState = newState;

      if (newState.hasFailed) {
        this.notificationService.showNotification(
          'Unable to sync with Withings',
          'error'
        );
      }
    });
  }
}
