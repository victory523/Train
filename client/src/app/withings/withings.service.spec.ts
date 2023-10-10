import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { WithingsService } from './withings.service';

function setup() {
  TestBed.configureTestingModule({
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      WithingsService,
    ],
  });
  const service = TestBed.inject(WithingsService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController };
}

describe('WithingsService', () => {
  describe('$sync', () => {
    it('should sync with Withings', () => {
      const { service, httpTestingController } = setup();
      service.$sync.subscribe();
      const request = httpTestingController.expectOne('/api/withings/sync');
      request.flush({});
      expect(request.request.method).toBe('POST');
      httpTestingController.verify();
    });
  });
});
