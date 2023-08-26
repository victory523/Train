import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BackupService } from './backup.service';

function setup() {
  TestBed.configureTestingModule({
    imports: [HttpClientTestingModule]
  });
  const service = TestBed.inject(BackupService);
  const httpTestingController = TestBed.inject(HttpTestingController)
  return {service, httpTestingController}
}

describe('BackupService', () => {
  describe('getLastBackupTime', () => {
    it('should return last backup time',  () => {
      const mockTime = new Date();
      const { service, httpTestingController } = setup();
      service.getLastBackupTime().subscribe(lastBackup => {
        expect(lastBackup).toEqual({ time: mockTime });
      })
      httpTestingController.expectOne('/db/last-backup-time').flush(mockTime.toISOString())
      httpTestingController.verify();
    });

    it('should return error message if last backup was more that 1 day ago',  () => {
      const mockTime = new Date(Date.now() - 25 * 60 * 60 * 1000 );
      const { service, httpTestingController } = setup();
      service.getLastBackupTime().subscribe(lastBackup => {
        expect(lastBackup).toEqual({ time: mockTime, errorMessage: 'No backup since 1 day' });
      })
      httpTestingController.expectOne('/db/last-backup-time').flush(mockTime.toISOString())
      httpTestingController.verify();
    });
  });
});
