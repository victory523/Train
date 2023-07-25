import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { WeightResponse } from './types';
import { WeightService } from './weight.service';

function setup() {
  TestBed.configureTestingModule({
    imports: [HttpClientTestingModule]
  });
  const service = TestBed.inject(WeightService);
  const httpTestingController = TestBed.inject(HttpTestingController)
  return {service, httpTestingController}
}

describe('WeightService', () => {
  describe('getWeight', () => {
    it('should return weight',  () => {
      const { service, httpTestingController } = setup();
      service.getWeight().subscribe(weight => {
        expect(weight).toEqual(89.6);
      })
      httpTestingController.expectOne('/api/weight').flush({ weight: 89.6 } as WeightResponse)
      httpTestingController.verify();
    });
  });
});
