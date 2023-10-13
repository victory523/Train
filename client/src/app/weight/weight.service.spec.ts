import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { WeightMeasurement, WeightService } from './weight.service';
import { NotificationService } from '../common-components/notification.service';
import { Subject } from 'rxjs';
import { WithingsService } from '../withings/withings.service';

function setup() {
  const mockNotificationService: jasmine.SpyObj<NotificationService> =
    jasmine.createSpyObj(['showNotification']);
  const syncMeasurements = new Subject<void>();
  const mockWithingsService: jasmine.SpyObj<WithingsService> =
    jasmine.createSpyObj(['syncMeasurements']);
  mockWithingsService.syncMeasurements.and.returnValue(
    syncMeasurements.asObservable()
  );
  TestBed.configureTestingModule({
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      WeightService,
      {
        provide: NotificationService,
        useValue: mockNotificationService,
      },
      {
        provide: WithingsService,
        useValue: mockWithingsService,
      },
    ],
  });
  const service = TestBed.inject(WeightService);
  const httpTestingController = TestBed.inject(HttpTestingController);
  return {
    service,
    httpTestingController,
    mockNotificationService,
    mockWithingsService,
    syncMeasurements,
  };
}

function daysBefore(days: number): Date {
  return new Date(new Date().getTime() - days * 1000 * 60 * 60 * 24);
}

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

const mockResponse2: WeightMeasurement[] = [
  { date: new Date('2020-03-05T00:00:00.000Z'), weight: 109.9 },
  { date: new Date('2020-03-07T00:00:00.000Z'), weight: 109.1 },
  { date: new Date('2020-05-05T00:00:00.000Z'), weight: 108.9 },
  { date: new Date('2020-05-07T00:00:00.000Z'), weight: 108.3 },
  {
    date: new Date('2020-05-10T00:00:00.000Z'),
    weight: 107.8,
    fatRatio: 31.04,
    fatMassWeight: 21.34,
  },
];

describe('WeightService', () => {
  describe('getWeight', () => {
    it('should return weight', () => {
      const { service, httpTestingController, syncMeasurements } = setup();
      service.getWeight(7).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      syncMeasurements.next();
      httpTestingController
        .expectOne('/api/weight?period=7')
        .flush(mockResponse);
      httpTestingController.verify();
    });

    it('should sync measurements first', () => {
      const { service, httpTestingController } = setup();
      service.getWeight(7).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      const request = httpTestingController.expectNone('/api/weight?period=7');
      expect(request).toBeUndefined();
      httpTestingController.verify();
    });

    it('should show notification if fetching weight measurements was not succesful', () => {
      const {
        service,
        httpTestingController,
        mockNotificationService,
        syncMeasurements,
      } = setup();
      service.getWeight(7).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      syncMeasurements.next();
      httpTestingController
        .expectOne('/api/weight?period=7')
        .error(new ProgressEvent(''));
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'Unable to fetch weight',
        'error'
      );
    });

    it('caches weight measurements', () => {
      const { service, httpTestingController, syncMeasurements } = setup();
      service.getWeight(7).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      syncMeasurements.next();
      service.getWeight(7).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      httpTestingController
        .expectOne('/api/weight?period=7')
        .flush(mockResponse);
      httpTestingController.verify();
    });

    it('caches weight measurements for multiple periods', () => {
      const { service, httpTestingController, syncMeasurements } = setup();
      service.getWeight(10).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      service.getWeight(30).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse2);
      });
      syncMeasurements.next();
      service.getWeight(10).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      service.getWeight(30).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse2);
      });
      service.getWeight(10).subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse);
      });
      httpTestingController
        .expectOne('/api/weight?period=10')
        .flush(mockResponse);
      httpTestingController
        .expectOne('/api/weight?period=30')
        .flush(mockResponse2);
      httpTestingController.verify();
    });
  });

  describe('getTodayWeight', () => {
    it('should return todays weight', () => {
      const { service, httpTestingController, syncMeasurements } = setup();
      service.getTodayWeight().subscribe((measurement) => {
        expect(measurement).toEqual(mockResponse.at(-1));
      });
      syncMeasurements.next();
      httpTestingController
        .expectOne('/api/weight?period=1')
        .flush([mockResponse.at(-1)]);
      httpTestingController.verify();
    });

    it('should return last todays weight', () => {
      const { service, syncMeasurements, httpTestingController } = setup();
      service.getTodayWeight().subscribe((measurement) => {
        expect(measurement).toEqual(mockResponse.at(-1));
      });
      syncMeasurements.next();
      httpTestingController
        .expectOne('/api/weight?period=1')
        .flush(mockResponse);
      httpTestingController.verify();
    });

    it('should return undefined if there was no weight measurements today', () => {
      const { service, syncMeasurements, httpTestingController } = setup();
      service.getTodayWeight().subscribe((measurement) => {
        expect(measurement).toBeUndefined();
      });
      syncMeasurements.next();
      httpTestingController.expectOne('/api/weight?period=1').flush([]);
      httpTestingController.verify();
    });

    it('should sync measurements first', () => {
      const { service, httpTestingController } = setup();
      service.getTodayWeight().subscribe();
      const request = httpTestingController.expectNone('/api/weight?period=1');
      expect(request).toBeUndefined();
      httpTestingController.verify();
    });

    it('should show notification if fetching today weight was not succesful', () => {
      const {
        service,
        httpTestingController,
        mockNotificationService,
        syncMeasurements,
      } = setup();
      service.getTodayWeight().subscribe((measurement) => {
        expect(measurement).toEqual(mockResponse.at(-1));
      });
      syncMeasurements.next();
      httpTestingController
        .expectOne('/api/weight?period=1')
        .error(new ProgressEvent(''));
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'Unable to fetch weight',
        'error'
      );
    });

    it('caches weight measurements', () => {
      const { service, httpTestingController, syncMeasurements } = setup();
      service.getTodayWeight().subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse.at(-1));
      });
      syncMeasurements.next();
      service.getTodayWeight().subscribe((measurements) => {
        expect(measurements).toEqual(mockResponse.at(-1));
      });
      httpTestingController
        .expectOne('/api/weight?period=1')
        .flush(mockResponse);
      httpTestingController.verify();
    });
  });

  describe('getDiff', () => {
    it('returns measurement diff between first and last day', () => {
      const { service, syncMeasurements, httpTestingController } = setup();
      service.getDiff(7).subscribe((diff) => {
        expect(diff?.weight.toFixed(3)).toBe('-0.017');
        expect(diff?.fatMassWeight?.toFixed(3)).toBe('-0.056');
        expect(diff?.fatRatio?.toFixed(3)).toBe('-0.033');
      });
      syncMeasurements.next();
      httpTestingController.expectOne('/api/weight?period=7').flush([
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
      ]);
      httpTestingController.verify();
    });

    it('returns only available measurements', () => {
      const { service, syncMeasurements, httpTestingController } = setup();
      service.getDiff(7).subscribe((diff) => {
        expect(diff?.weight.toFixed(3)).toBe('-0.017');
      });
      syncMeasurements.next();
      httpTestingController.expectOne('/api/weight?period=7').flush([
        {
          date: daysBefore(3),
          weight: 108.9,
        },
        {
          date: daysBefore(0),
          weight: 107.1,
        },
      ]);
      httpTestingController.verify();
    });

    it('returns undefined if there is only 1 measurements', () => {
      const { service, syncMeasurements, httpTestingController } = setup();
      service.getDiff(7).subscribe((diff) => {
        expect(diff).toBeUndefined();
      });
      syncMeasurements.next();
      httpTestingController.expectOne('/api/weight?period=7').flush([
        {
          date: daysBefore(3),
          weight: 108.9,
        },
      ]);
      httpTestingController.verify();
    });

    it('returns undefined if there are no measurements', () => {
      const { service, syncMeasurements, httpTestingController } = setup();
      service.getDiff(7).subscribe((diff) => {
        expect(diff).toBeUndefined();
      });
      syncMeasurements.next();
      httpTestingController.expectOne('/api/weight?period=7').flush([]);
      httpTestingController.verify();
    });

    it('should sync measurements first', () => {
      const { service, httpTestingController } = setup();
      service.getDiff(7).subscribe();
      const request = httpTestingController.expectNone('/api/weight?period=1');
      expect(request).toBeUndefined();
      httpTestingController.verify();
    });

    it('should show notification if fetching today weight was not succesful', () => {
      const {
        service,
        httpTestingController,
        mockNotificationService,
        syncMeasurements,
      } = setup();
      service.getDiff(7).subscribe();
      syncMeasurements.next();
      httpTestingController
        .expectOne('/api/weight?period=7')
        .error(new ProgressEvent(''));
      httpTestingController.verify();
      expect(mockNotificationService.showNotification).toHaveBeenCalledWith(
        'Unable to fetch weight',
        'error'
      );
    });

    it('caches weight measurements', () => {
      const { service, httpTestingController, syncMeasurements } = setup();
      service.getDiff(7).subscribe((diff) => {
        expect(diff?.weight.toFixed(3)).toEqual('-0.010');
      });
      syncMeasurements.next();
      service.getDiff(7).subscribe((diff) => {
        expect(diff?.weight.toFixed(3)).toEqual('-0.010');
      });
      httpTestingController
        .expectOne('/api/weight?period=7')
        .flush(mockResponse);
      httpTestingController.verify();
    });
  });

  it('caches across getWeight, getTodayWeight and getDiff functions', () => {
    const { service, httpTestingController, syncMeasurements } = setup();
    service.getWeight(1).subscribe();
    service.getTodayWeight().subscribe();
    service.getDiff(1).subscribe();
    syncMeasurements.next();
    const request = httpTestingController.expectOne('/api/weight?period=1');
    request.flush(mockResponse);
    expect(request.request.method).toBe('GET');
    httpTestingController.verify();
  });
});
