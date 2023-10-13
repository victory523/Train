import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  EMPTY,
  Observable,
  catchError,
  map,
  mergeMap,
  shareReplay,
  switchMap,
} from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { WithingsService } from '../withings/withings.service';

export type WeightMeasurement = {
  date: Date;
  weight: number;
  fatRatio?: number;
  fatMassWeight?: number;
};

@Injectable()
export class WeightService {
  constructor(
    private readonly http: HttpClient,
    private readonly withingsService: WithingsService,
    private readonly notificationService: NotificationService
  ) {}

  private readonly cache: Record<number, Observable<WeightMeasurement[]>> = {};

  getWeight(period = 0) {
    if (this.cache[period]) {
      return this.cache[period];
    }

    this.cache[period] = this.withingsService.syncMeasurements().pipe(
      mergeMap(() =>
        this.http
          .get<WeightMeasurement[]>('/api/weight', {
            params: { ...(period ? { period } : {}) },
          })
          .pipe(
            map((measurements) =>
              measurements.map((measurement) => ({
                ...measurement,
                date: new Date(measurement.date),
              }))
            ),
            catchError(() => {
              this.notificationService.showNotification(
                'Unable to fetch weight',
                'error'
              );
              return EMPTY;
            })
          )
      ),
      shareReplay(1)
    );

    return this.cache[period];
  }

  getTodayWeight() {
    return this.getWeight(1).pipe(map((measurements) => measurements.at(-1)));
  }

  getDiff(period = 0) {
    return this.getWeight(period).pipe(
      map((measurements) => {
        if (measurements.length < 2) {
          return undefined;
        }

        const initial = measurements[0];
        const latest = measurements[measurements.length - 1];

        return {
          date: latest.date,
          weight: (latest.weight - initial.weight) / initial.weight,
          ...(initial.fatMassWeight &&
            latest.fatMassWeight && {
              fatMassWeight:
                (latest.fatMassWeight - initial.fatMassWeight) /
                initial.fatMassWeight,
            }),
          ...(initial.fatRatio &&
            latest.fatRatio && {
              fatRatio: (latest.fatRatio - initial.fatRatio) / initial.fatRatio,
            }),
        };
      })
    );
  }
}
