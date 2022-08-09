import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WeightResponse } from './types';

@Injectable({
  providedIn: 'root'
})
export class WithingsService {

  constructor(
    private http: HttpClient
  ) { }

  getWeight(): Observable<WeightResponse> {
    return this.http.get<WeightResponse>('/api/withings/weight');
  }
}
