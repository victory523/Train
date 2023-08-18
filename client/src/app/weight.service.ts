import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';

export type WeightMeasurement = {
  weight: number;
  date: Date;
};

@Injectable({
  providedIn: 'root',
})
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

  getTodayWeight(measurements: WeightMeasurement[]): number | undefined {
    const start = new Date();
    start.setUTCHours(0, 0, 0, 0);
    const end = new Date();
    end.setUTCHours(23, 59, 59, 999);

    return measurements.findLast(
      ({ date }) =>
        start.getTime() < date.getTime() && date.getTime() < end.getTime()
    )?.weight;
  }
}
