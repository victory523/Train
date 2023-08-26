import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { WithingsService } from './withings.service';

function setup() {
  const mockLocation = jasmine.createSpyObj(['assign']) as Location;
  TestBed.configureTestingModule({
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      { provide: Location, useValue: mockLocation },
      WithingsService,
    ],
  });
  const service = TestBed.inject(WithingsService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController, mockLocation };
}

describe('WithingsService', () => {
  describe('sync', () => {
    it('should sync with Withings', () => {
      const { service, httpTestingController } = setup();
      service.sync().subscribe();
      const request = httpTestingController.expectOne('/api/withings/sync');
      request.flush({});
      expect(request.request.method).toBe('POST');
      httpTestingController.verify();
    });

    it('should open authorization page if 401 is returned', async () => {
      const { service, mockLocation, httpTestingController } = setup();
      service.sync().subscribe();
      const request = httpTestingController.expectOne('/api/withings/sync');
      request.flush(
        { _links: { oauth2Login: { href: '/withings/auth' } } },
        { status: 401, statusText: 'Unauthorized' }
      );
      expect(mockLocation.assign).toHaveBeenCalledWith('/withings/auth');
      httpTestingController.verify();
    });
  });
});
