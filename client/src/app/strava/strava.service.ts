import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EMPTY,
  catchError,
  concat,
  mergeMap,
  shareReplay
} from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { WithingsService } from '../withings/withings.service';

@Injectable()
export class StravaService {
  constructor(
    private readonly http: HttpClient,
    private readonly notificationService: NotificationService,
    private readonly withingsService: WithingsService
  ) {}

  private readonly $syncActivities = concat(
    this.withingsService.syncMeasurements(),
    this.http.post<void>('/api/strava/activities/sync', undefined).pipe(
      mergeMap(() => EMPTY),
      catchError((e) => {
        this.notificationService.showNotification(
          'Unable to sync with Strava',
          'error'
        );
        return EMPTY;
      }),
      shareReplay(1)
    )
  );

  syncActivities() {
    return this.$syncActivities;
  }
}
