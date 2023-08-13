import { Location } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WithingsService {
  constructor(private http: HttpClient, private location: Location) {}

  sync(): Observable<void> {
    return this.http.post<void>('/api/withings/sync', undefined).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          this.location.go(error.error._links.oauth2Login.href)
          return of(undefined);
        }

        throw error;
      })
    );
  }
}
