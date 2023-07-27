import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';

export type LastBackup = {
  time: Date;
  errorMessage?: string;
};

@Injectable({
  providedIn: 'root',
})
export class BackupService {
  constructor(private http: HttpClient) {}

  getLastBackupTime(): Observable<LastBackup> {
    return this.http.get<Date>('/db/last-backup-time').pipe(
      map((date) => ({
        time: new Date(date),
        ...(new Date(date).getTime() + 24 * 60 * 60 * 1000 < Date.now() && { errorMessage: 'No backup since 1 day' })
      }))
    );
  }
}
