import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { WithingsService } from './withings.service';
import { NotificationService } from '../common-components/notification.service';

function setup() {
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  TestBed.configureTestingModule({
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      WithingsService,
      { provide: NotificationService, useValue: mockNotificationService },
    ],
  });
  const service = TestBed.inject(WithingsService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController, mockNotificationService };
}

describe('WithingsService', () => {
  describe('sync', () => {
    it('should sync with Withings', () => {
      const { service, httpTestingController } = setup();
      service.syncMeasurements().subscribe();
      const request = httpTestingController.expectOne('/api/withings/sync');
      request.flush({});
      expect(request.request.method).toBe('POST');
      httpTestingController.verify();
    });

    it('should show notification if fetching last backup was not succesful', () => {
      const { service, httpTestingController, mockNotificationService } =
        setup();
      service.syncMeasurements().subscribe();
      httpTestingController
        .expectOne('/api/withings/sync')
        .error(new ProgressEvent(''));
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'Unable to sync with Withings',
        'error'
      );
    });

    it('caches last backup time', () => {
      const { service, httpTestingController } = setup();
      service.syncMeasurements().subscribe();
      service.syncMeasurements().subscribe();
      const request = httpTestingController.expectOne('/api/withings/sync');
      request.flush({});
      expect(request.request.method).toBe('POST');
      httpTestingController.verify();
    });
  });
});
