import { HttpClient } from '@angular/common/http';
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

  getWeight(): Observable<WeightMeasurement[]> {
    return this.http.get<WeightMeasurement[]>('/api/weight');
  }
}
