import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, of, shareReplay } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';

@Injectable()
export class StravaService {
  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  $syncActivities = this.http
    .post<void>('/api/strava/activities/sync', undefined)
    .pipe(
      catchError(() => {
        this.notificationService.showNotification(
          'Unable to sync with Strava',
          'error'
        );
        return of();
      }),
      shareReplay(1)
    );
}
