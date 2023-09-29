import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, of, shareReplay } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';

@Injectable()
export class WithingsService {
  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  $sync = this.http.post<void>('/api/withings/sync', undefined).pipe(
    catchError(() => {
      this.notificationService.showNotification(
        'Unable to sync with Withings',
        'error'
      );
      return of();
    }),
    shareReplay(1)
  );
}
