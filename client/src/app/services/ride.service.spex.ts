import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RideService, RideStats } from './ride.service';

function setup() {
  TestBed.configureTestingModule({
    providers: [provideHttpClient(), provideHttpClientTesting(), RideService],
  });
  const service = TestBed.inject(RideService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController };
}

describe('RideService', () => {
  describe('getRideStats', () => {
    it('should return weight', () => {
      const { service, httpTestingController } = setup();
      const mockResponse: RideStats = { calories: 646, elevationGain: 408, distance: 11747.7, time: 3074 };
      service.getRideStats(7).subscribe((rideStats) => {
        expect(rideStats).toEqual(mockResponse);
      });
      httpTestingController
        .expectOne('/api/ride/stats?period=7')
        .flush(mockResponse);
      httpTestingController.verify();
    });
  });
});
