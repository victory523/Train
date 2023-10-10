import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';
import { NotificationService } from '../common-components/notification.service';
import { RideService, RideStats } from './ride.service';
import { StravaService } from '../strava/strava.service';

function setup() {
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  const syncActivities = new Subject<void>();
  const mockStravaService = {
    $syncActivities: syncActivities.asObservable(),
  };
  TestBed.configureTestingModule({
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      RideService,
      { provide: NotificationService, useValue: mockNotificationService },
      { provide: StravaService, useValue: mockStravaService },
    ],
  });
  const service = TestBed.inject(RideService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return {
    service,
    httpTestingController,
    syncActivities,
    mockNotificationService,
  };
}
const mockResponse: RideStats = {
  calories: 646,
  elevationGain: 408,
  distance: 11747.7,
  time: 3074,
};
const mockResponse2: RideStats = {
  calories: 2 * 646,
  elevationGain: 2 * 408,
  distance: 2 * 11747.7,
  time: 2 * 3074,
};

describe('RideService', () => {
  describe('getRideStats', () => {
    it('should return ride stats for given period', () => {
      const { service, httpTestingController, syncActivities } = setup();
      service.getRideStats(30).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse);
      });
      syncActivities.next();
      httpTestingController
        .expectOne('/api/ride/stats?period=30')
        .flush(mockResponse);
      httpTestingController.verify();
    });

    it('should show notification if fetching ride stats was not succesful', () => {
      const {
        service,
        httpTestingController,
        mockNotificationService,
        syncActivities,
      } = setup();
      service.getRideStats(30).subscribe((rideStats) => {
        expect(rideStats).toBeUndefined();
      });
      syncActivities.next();
      httpTestingController
        .expectOne('/api/ride/stats?period=30')
        .error(new ProgressEvent(''));
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'Unable to fetch ride stats',
        'error'
      );
    });

    it('caches ride stats', () => {
      const { service, httpTestingController, syncActivities } = setup();
      service.getRideStats(30).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse);
      });
      syncActivities.next();
      service.getRideStats(30).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse);
      });
      httpTestingController
        .expectOne('/api/ride/stats?period=30')
        .flush(mockResponse);
      httpTestingController.verify();
    });

    it('caches ride stats for multiple periods', () => {
      const { service, httpTestingController, syncActivities } = setup();
      service.getRideStats(10).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse);
      });
      service.getRideStats(30).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse2);
      });
      syncActivities.next();
      service.getRideStats(10).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse);
      });
      service.getRideStats(30).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse2);
      });
      service.getRideStats(10).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse);
      });
      httpTestingController
        .expectOne('/api/ride/stats?period=10')
        .flush(mockResponse);
      httpTestingController
        .expectOne('/api/ride/stats?period=30')
        .flush(mockResponse2);
      httpTestingController.verify();
    });
  });
});
