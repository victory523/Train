
import { of } from 'rxjs';
import { WeightService } from './weight.service';
import { WeightResponse } from './types';

async function setup() {
  const mockHttpClient = jasmine.createSpyObj('HttpClient', {
    get: of({ weight: 89.6 } as WeightResponse ),
  });

  const service = new WeightService(mockHttpClient);

  return {
    service,
  };
}

describe('WeightService', () => {
  it('should be created', async () => {
    const { service } = await setup()
    expect(service).toBeTruthy();
  });
});
