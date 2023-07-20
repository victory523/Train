import { Observable, of, throwError } from 'rxjs';
import { WithingsService } from './withings.service';
import { HttpErrorResponse } from '@angular/common/http';

async function setup(
  { $sync }: { $sync: Observable<void> } = { $sync: of(undefined) }
) {
  const locationAssign = jasmine.createSpy();
  const mockHttpClient = jasmine.createSpyObj('HttpClient', ['post']);

  mockHttpClient.post.and.callFake((url: string) =>
    url === '/api/withings/sync' ? $sync : of()
  );

  const service = new WithingsService(mockHttpClient, { assign: locationAssign } as unknown as Location );

  return {
    service,
    locationAssign
  };
}

describe('WithingsService', () => {
  describe('sync', () => {
    it('should return undefined', async () => {
      const { service } = await setup();
      const response = await new Promise((resolve) =>
        service.sync().subscribe(resolve)
      );
      expect(response).toBe(undefined);
    });

    it('should open authorization page if 401 is returned', async () => {
      const { service, locationAssign } = await setup({
        $sync: throwError(
          () =>
            new HttpErrorResponse({
              status: 401,
              error: { _links: { oauth2Login: { href: '/withings/auth' } } },
            })
        ),
      });
      const response = await new Promise((resolve) =>
        service.sync().subscribe(resolve)
      );
      expect(locationAssign).toHaveBeenCalledWith('/withings/auth')
    });
  });
});
