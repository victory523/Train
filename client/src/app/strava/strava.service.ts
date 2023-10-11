import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, catchError, shareReplay } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';

@Injectable()
export class StravaService {
  constructor(
    private readonly http: HttpClient,
    private readonly notificationService: NotificationService
  ) {}

  private readonly $syncActivities = this.http
    .post<void>('/api/strava/activities/sync', undefined)
    .pipe(
      catchError(() => {
        this.notificationService.showNotification(
          'Unable to sync with Strava',
          'error'
        );
        return EMPTY;
      }),
      shareReplay(1)
    );

  syncActivities() {
    return this.$syncActivities;
  }
}
