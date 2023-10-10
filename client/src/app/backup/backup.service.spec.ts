import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { NotificationService } from '../common-components/notification.service';
import { BackupService } from './backup.service';

function setup() {
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  TestBed.configureTestingModule({
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      BackupService,
      { provide: NotificationService, useValue: mockNotificationService },
    ],
  });
  const service = TestBed.inject(BackupService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController, mockNotificationService };
}

describe('BackupService', () => {
  describe('getLastBackupTime', () => {
    it('should return last backup time', () => {
      const mockTime = new Date();
      const { service, httpTestingController } = setup();
      service.getLastBackupTime().subscribe((lastBackup) => {
        expect(lastBackup).toEqual(mockTime);
      });
      httpTestingController
        .expectOne('/db/last-backup-time')
        .flush(mockTime.toISOString());
      httpTestingController.verify();
    });

    it('should show notification if last backup was more that 1 day ago', () => {
      const mockTime = new Date(Date.now() - 25 * 60 * 60 * 1000);
      const { service, httpTestingController, mockNotificationService } =
        setup();
      service.getLastBackupTime().subscribe((lastBackup) => {
        expect(lastBackup).toEqual(mockTime);
      });
      httpTestingController
        .expectOne('/db/last-backup-time')
        .flush(mockTime.toISOString());
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'No backup since 1 day',
        'error'
      );
    });

    it('should show notification if fetching last backup was not succesful', () => {
      const { service, httpTestingController, mockNotificationService } =
        setup();
      service.getLastBackupTime().subscribe((lastBackup) => {
        expect(lastBackup).toBeUndefined();
      });
      httpTestingController
        .expectOne('/db/last-backup-time')
        .error(new ProgressEvent(''));
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'Unable to fetch last backup time',
        'error'
      );
    });

    it('caches last backup time', () => {
      const mockTime = new Date();
      const { service, httpTestingController } = setup();
      service.getLastBackupTime().subscribe((lastBackup) => {
        expect(lastBackup).toEqual(mockTime);
      });
      service.getLastBackupTime().subscribe((lastBackup) => {
        expect(lastBackup).toEqual(mockTime);
      });
      httpTestingController
        .expectOne('/db/last-backup-time')
        .flush(mockTime.toISOString());
      httpTestingController.verify();
    });
  });
});
