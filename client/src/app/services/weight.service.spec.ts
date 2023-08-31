import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { WeightMeasurement, WeightService } from './weight.service';

function setup() {
  TestBed.configureTestingModule({
    providers: [provideHttpClient(), provideHttpClientTesting(), WeightService],
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
        {
          date: new Date('2020-05-10T00:00:00.000Z'),
          weight: 107.8,
          fatRatio: 31.04,
          fatMassWeight: 21.34,
        },
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
        {
          date: daysBefore(0),
          weight: 107.8,
          fatRatio: 31.04,
          fatMassWeight: 21.34,
        },
      ];
      expect(service.getTodayWeight(measurements)).toEqual({
        date: daysBefore(0),
        weight: 107.8,
        fatRatio: 31.04,
        fatMassWeight: 21.34,
      });
    });

    it('should return last todays weight', () => {
      const { service } = setup();
      const measurements: WeightMeasurement[] = [
        { date: daysBefore(3), weight: 108.9 },
        { date: daysBefore(1), weight: 108.3 },
        { date: daysBefore(0), weight: 107.8 },
        {
          date: daysBefore(0),
          weight: 107.7,
          fatRatio: 31.04,
          fatMassWeight: 21.34,
        },
      ];
      expect(service.getTodayWeight(measurements)).toEqual({
        date: daysBefore(0),
        weight: 107.7,
        fatRatio: 31.04,
        fatMassWeight: 21.34,
      });
    });

    it('should return undefined if there was no weight measurements today', () => {
      const { service } = setup();
      const measurements: WeightMeasurement[] = [
        { date: daysBefore(3), weight: 108.9 },
        {
          date: daysBefore(1),
          weight: 108.3,
          fatRatio: 31.04,
          fatMassWeight: 21.34,
        },
      ];
      expect(service.getTodayWeight(measurements)).toBeUndefined();
    });
  });

  describe('getDiff', () => {
    it('returns measurement diff between first and last day', () => {
      const { service } = setup();
      const measurements: WeightMeasurement[] = [
        {
          date: daysBefore(3),
          weight: 108.9,
          fatMassWeight: 22.6,
          fatRatio: 32.1,
        },
        {
          date: daysBefore(0),
          weight: 107.1,
          fatMassWeight: 21.34,
          fatRatio: 31.04,
        },
      ];
      expect(service.getDiff(measurements)?.weight.toFixed(3)).toBe('-0.017');
      expect(service.getDiff(measurements)?.fatMassWeight?.toFixed(3)).toBe(
        '-0.056'
      );
      expect(service.getDiff(measurements)?.fatRatio?.toFixed(3)).toBe(
        '-0.033'
      );
    });
  });

  it('returns only available measurements', () => {
    const { service } = setup();
    const measurements: WeightMeasurement[] = [
      {
        date: daysBefore(3),
        weight: 108.9,
      },
      {
        date: daysBefore(0),
        weight: 107.1,
      },
    ];
    expect(service.getDiff(measurements)?.weight.toFixed(3)).toBe('-0.017');
  });

  it('returns undefined if there is only 1 measurements', () => {
    const { service } = setup();
    const measurements: WeightMeasurement[] = [
      {
        date: daysBefore(3),
        weight: 108.9,
      },
    ];
    expect(service.getDiff(measurements)).toBeUndefined();
  });

  it('returns undefined if there are no measurements', () => {
    const { service } = setup();
    expect(service.getDiff([])).toBeUndefined();
  });
});
