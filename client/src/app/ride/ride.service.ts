import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EMPTY,
  Observable,
  catchError,
  concat,
  shareReplay
} from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { StravaService } from '../strava/strava.service';

export type RideStats = {
  calories?: number;
  elevationGain?: number;
  distance?: number;
  time?: number;
};

@Injectable()
export class RideService {
  constructor(
    private readonly http: HttpClient,
    private readonly stravaService: StravaService,
    private readonly notificationService: NotificationService
  ) {}

  private readonly cache: Record<number, Observable<RideStats>> = {};

  getRideStats(period = 0) {
    if (this.cache[period]) {
      return this.cache[period];
    }

    this.cache[period] = concat(
      this.stravaService.syncActivities(),
      this.http
        .get<RideStats>('/api/ride/stats', {
          params: {
            ...(period ? { period } : {}),
          },
        })
        .pipe(
          catchError(() => {
            this.notificationService.showNotification(
              'Unable to fetch ride stats',
              'error'
            );
            return EMPTY;
          }),
          shareReplay(1)
        )
    );

    return this.cache[period];
  }
}
