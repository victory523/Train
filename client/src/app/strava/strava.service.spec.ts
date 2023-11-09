import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { EMPTY } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { WithingsService } from '../withings/withings.service';
import { StravaService } from './strava.service';

function setup() {
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  const mockWithingsService: jasmine.SpyObj<WithingsService> =
    jasmine.createSpyObj(['syncMeasurements']);

  mockWithingsService.syncMeasurements.and.returnValue(EMPTY);

  TestBed.configureTestingModule({
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      StravaService,
      { provide: NotificationService, useValue: mockNotificationService },
      { provide: WithingsService, useValue: mockWithingsService },
    ],
  });
  const service = TestBed.inject(StravaService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController, mockNotificationService };
}

describe('StravaService', () => {
  describe('syncActivities', () => {
    it('should sync with Strava', () => {
      const { service, httpTestingController } = setup();
      service.syncActivities().subscribe();
      const request = httpTestingController.expectOne(
        '/api/strava/activities/sync'
      );
      request.flush({});
      expect(request.request.method).toBe('POST');
      httpTestingController.verify();
    });

    it('should show notification if fetching last backup was not succesful', () => {
      const { service, httpTestingController, mockNotificationService } =
        setup();
      service.syncActivities().subscribe();
      httpTestingController
        .expectOne('/api/strava/activities/sync')
        .error(new ProgressEvent(''));
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'Unable to sync with Strava',
        'error'
      );
    });

    it('caches last backup time', () => {
      const { service, httpTestingController } = setup();
      service.syncActivities().subscribe();
      service.syncActivities().subscribe();
      const request = httpTestingController.expectOne(
        '/api/strava/activities/sync'
      );
      request.flush({});
      expect(request.request.method).toBe('POST');
      httpTestingController.verify();
    });
  });
});
