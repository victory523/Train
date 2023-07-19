import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WithingsService {
  constructor(private http: HttpClient) {}

  sync(): Observable<void> {
    return this.http.post<void>('/api/withings/sync', undefined).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          window.location.href = error.error._links.oauth2Login.href;
          return of();
        }

        throw error;
      })
    );
  }
}
