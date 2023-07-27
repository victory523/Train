import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';

export interface WeightResponse {
  weight?: number;
}

@Injectable({
  providedIn: 'root',
})
export class WeightService {
  constructor(private http: HttpClient) {}

  getWeight(): Observable<number | undefined> {
    return this.http
      .get<WeightResponse>('/api/weight')
      .pipe(map((weightResponse) => weightResponse.weight));
  }
}
