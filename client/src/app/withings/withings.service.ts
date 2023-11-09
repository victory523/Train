import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, catchError, mergeMap, shareReplay } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';

@Injectable()
export class WithingsService {
  constructor(
    private readonly http: HttpClient,
    private readonly notificationService: NotificationService
  ) {}

  private readonly $syncMeasurements = this.http
    .post<void>('/api/withings/sync', undefined)
    .pipe(
      mergeMap(() => EMPTY),
      catchError((e) => {
        this.notificationService.showNotification(
          'Unable to sync with Withings',
          'error'
        );
        return EMPTY;
      }),
      shareReplay(1)
    );

  syncMeasurements() {
    return this.$syncMeasurements;
  }
}
