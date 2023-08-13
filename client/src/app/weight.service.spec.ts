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
      httpTestingController.expectOne('/api/weight').flush(mockResponse);
      httpTestingController.verify();
    });
  });
});
