import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WithingsService {
  constructor(private http: HttpClient, private location: Location) {}

  sync(): Observable<void> {
    return this.http
      .post<void>('/api/withings/sync', {
        headers: {
          'X-Timezone': Intl.DateTimeFormat().resolvedOptions().timeZone,
        },
      })
      .pipe(
        catchError((error) => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            this.location.assign(error.error._links.oauth2Login.href);
            return of(undefined);
          }

          throw error;
        })
      );
  }
}
