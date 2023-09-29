import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  catchError,
  mergeMap,
  of,
  shareReplay,
  switchMap,
} from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { StravaService } from './strava.service';

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
  private readonly selectedPeriodSubject = new BehaviorSubject<
    number | undefined
  >(undefined);
  private readonly $selectedPeriod = this.selectedPeriodSubject.asObservable();

  selectPeriod(newPeriod?: number) {
    this.selectedPeriodSubject.next(newPeriod);
  }

  private getRideStats(period = 0) {
    if (this.cache[period]) {
      return this.cache[period];
    }

    this.cache[period] = this.http
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
          return of();
        }),
        shareReplay(1)
      );

    return this.cache[period];
  }

  $periodRideStats = this.stravaService.$syncActivities.pipe(
    mergeMap(() =>
      this.$selectedPeriod.pipe(
        switchMap((period) => this.getRideStats(period))
      )
    )
  );

  $todayRideStats = this.stravaService.$syncActivities.pipe(
    mergeMap(() => this.getRideStats(1))
  );
}
