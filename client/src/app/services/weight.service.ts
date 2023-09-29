import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  mergeMap,
  of,
  shareReplay,
  switchMap,
} from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { WithingsService } from './withings.service';

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

  private readonly selectedPeriodSubject = new BehaviorSubject<
    number | undefined
  >(undefined);
  private readonly $selectedPeriod = this.selectedPeriodSubject.asObservable();

  selectPeriod(newPeriod?: number) {
    this.selectedPeriodSubject.next(newPeriod);
  }

  private getWeight(period = 0) {
    if (this.cache[period]) {
      return this.cache[period];
    }

    this.cache[period] = this.http
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
          return of();
        }),
        shareReplay(1)
      );

    return this.cache[period];
  }

  $periodWeight = this.withingsService.$sync.pipe(
    mergeMap(() =>
      this.$selectedPeriod.pipe(switchMap((period) => this.getWeight(period)))
    )
  );

  $todayWeight = this.withingsService.$sync.pipe(
    mergeMap(() =>
      this.getWeight(1).pipe(map((measurements) => measurements.at(-1)))
    )
  );

  $diff = this.$periodWeight.pipe(
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
