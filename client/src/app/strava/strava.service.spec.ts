import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { StravaService } from './strava.service';

function setup() {
  TestBed.configureTestingModule({
    providers: [provideHttpClient(), provideHttpClientTesting(), StravaService],
  });
  const service = TestBed.inject(StravaService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController };
}

describe('StravaService', () => {
  describe('$syncActivities', () => {
    it('should sync with Strava', () => {
      const { service, httpTestingController } = setup();
      service.$syncActivities.subscribe();
      const request = httpTestingController.expectOne(
        '/api/strava/activities/sync'
      );
      request.flush({});
      expect(request.request.method).toBe('POST');
      httpTestingController.verify();
    });
  });
});
