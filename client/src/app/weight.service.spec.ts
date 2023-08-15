import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { WeightMeasurement, WeightService } from './weight.service';

function setup() {
  TestBed.configureTestingModule({
    imports: [HttpClientTestingModule],
  });
  const service = TestBed.inject(WeightService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return { service, httpTestingController };
}

function daysBefore(days: number): Date {
  return new Date(new Date().getTime() - days * 1000 * 60 * 60 * 24);
}

describe('WeightService', () => {
  describe('getWeight', () => {
    it('should return weight', () => {
      const { service, httpTestingController } = setup();
      const mockResponse: WeightMeasurement[] = [
        { date: new Date('2020-05-05T00:00:00.000Z'), weight: 108.9 },
        { date: new Date('2020-05-07T00:00:00.000Z'), weight: 108.3 },
        { date: new Date('2020-05-10T00:00:00.000Z'), weight: 107.8 },
      ];
      service.getWeight(7).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      httpTestingController
        .expectOne('/api/weight?period=7')
        .flush(mockResponse);
      httpTestingController.verify();
    });
  });

  describe('getTodayWeight', () => {
    it('should return todays weight', () => {
      const { service } = setup();
      const measurements: WeightMeasurement[] = [
        { date: daysBefore(3), weight: 108.9 },
        { date: daysBefore(1), weight: 108.3 },
        { date: daysBefore(0), weight: 107.8 },
      ];
      expect(service.getTodayWeight(measurements)).toBe(107.8);
    });

    it('should return last todays weight', () => {
      const { service } = setup();
      const measurements: WeightMeasurement[] = [
        { date: daysBefore(3), weight: 108.9 },
        { date: daysBefore(1), weight: 108.3 },
        { date: daysBefore(0), weight: 107.8 },
        { date: daysBefore(0), weight: 107.7 },
      ];
      expect(service.getTodayWeight(measurements)).toBe(107.7);
    });

    it('should return undefined if there was no weight measurements today', () => {
      const { service } = setup();
      const measurements: WeightMeasurement[] = [
        { date: daysBefore(3), weight: 108.9 },
        { date: daysBefore(1), weight: 108.3 },
      ];
      expect(service.getTodayWeight(measurements)).toBeUndefined();
    });
  });
});
