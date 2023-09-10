import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';

export type RideStats = {
  calories?: number;
  elevationGain?: number;
  distance?: number;
  time?: number;
};

@Injectable()
export class RideService {
  constructor(private http: HttpClient) {}

  getRideStats(period?: number): Observable<RideStats> {
    return this.http.get<RideStats>('/api/ride/stats', {
      params: { ...(period ? { period } : {}) },
    });
  }
}
