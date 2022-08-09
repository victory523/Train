import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { WeightResponse } from './types';

@Injectable({
  providedIn: 'root',
})
export class WithingsService {
  constructor(private http: HttpClient) {}

  getWeight(): Observable<number | undefined> {
    return this.http.get<WeightResponse>('/api/withings/weight').pipe(
      map((weightResponse) => weightResponse.weight),
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          debugger;
          window.location.href = error.error._links.oauth2Login.href;
        }
        
        return of(undefined);
      })
    );
  }
}
