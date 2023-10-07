import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, catchError, map, of, shareReplay, tap } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';

@Injectable()
export class BackupService {
  constructor(
    private readonly http: HttpClient,
    private readonly notificationService: NotificationService
  ) {}

  $lastBackupTime = this.http.get<Date>('/db/last-backup-time').pipe(
    map((date) => new Date(date)),
    tap((date) => {
      if (date.getTime() + 24 * 60 * 60 * 1000 < Date.now()) {
        this.notificationService.showNotification(
          'No backup since 1 day',
          'error'
        );
      }
    }),
    catchError(() => {
      this.notificationService.showNotification(
        'Unable to fetch last backup time',
        'error'
      );
      return EMPTY;
    }),
    shareReplay(1)
  );
}
