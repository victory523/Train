
import { of } from 'rxjs';
import { WithingsService } from './withings.service';

async function setup() {
  const mockHttpClient = jasmine.createSpyObj('HttpClient', {
    post: of(),
  });

  const service = new WithingsService(mockHttpClient);

  return {
    service,
  };
}

describe('WithingsService', () => {
  it('should be created', async () => {
    const { service } = await setup()
    expect(service).toBeTruthy();
  });
});
