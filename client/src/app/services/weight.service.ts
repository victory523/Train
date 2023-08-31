import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';

export type WeightMeasurement = {
  date: Date;
  weight: number;
  fatRatio?: number;
  fatMassWeight?: number;
};

@Injectable()
export class WeightService {
  constructor(private http: HttpClient) {}

  getWeight(period?: number): Observable<WeightMeasurement[]> {
    return this.http
      .get<WeightMeasurement[]>('/api/weight', {
        params: { ...(period ? { period } : {}) },
      })
      .pipe(
        map((measurements) =>
          measurements.map((measurement) => ({
            ...measurement,
            date: new Date(measurement.date),
          }))
        )
      );
  }

  getTodayWeight(
    measurements: WeightMeasurement[]
  ): WeightMeasurement | undefined {
    const start = new Date();
    start.setUTCHours(0, 0, 0, 0);
    const end = new Date();
    end.setUTCHours(23, 59, 59, 999);

    return measurements.findLast(
      ({ date }) =>
        start.getTime() < date.getTime() && date.getTime() < end.getTime()
    );
  }

  getDiff(measurements: WeightMeasurement[]): WeightMeasurement | undefined {
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
  }
}
