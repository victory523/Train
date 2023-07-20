import { of } from 'rxjs';
import { WeightResponse } from './types';
import { WeightService } from './weight.service';

async function setup() {
  const mockHttpClient = jasmine.createSpyObj('HttpClient', ['get']);

  mockHttpClient.get.and.callFake((url: string) =>
    url === '/api/weight' ? of({ weight: 89.6 } as WeightResponse) : of()
  );

  const service = new WeightService(mockHttpClient);

  return {
    service,
    mockHttpClient,
  };
}

describe('WeightService', () => {
  describe('getWeight', () => {
    it('should return weight', async () => {
      const { service } = await setup();
      const weight = await new Promise((resolve) =>
        service.getWeight().subscribe(resolve)
      );
      expect(weight).toEqual(89.6);
    });
  });
});
