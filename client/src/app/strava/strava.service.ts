import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, catchError, mergeMap, of, shareReplay } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { WithingsService } from '../withings/withings.service';

@Injectable()
export class StravaService {
  constructor(
    private readonly http: HttpClient,
    private readonly notificationService: NotificationService,
    private readonly withingsService: WithingsService
  ) {}

  private readonly $syncActivities = this.withingsService
    .syncMeasurements()
    .pipe(
      mergeMap(() =>
        this.http.post<void>('/api/strava/activities/sync', undefined).pipe(
          catchError(() => {
            this.notificationService.showNotification(
              'Unable to sync with Strava',
              'error'
            );
            return EMPTY;
          }),
          shareReplay(1)
        )
      )
    );

  syncActivities() {
    return this.$syncActivities;
  }
}
