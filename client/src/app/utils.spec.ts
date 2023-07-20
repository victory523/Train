import { Observable, of, take, throwError } from 'rxjs';
import { subscribeToRequestState } from './utils';

async function setup({
  $observable,
  takeCount,
}: {
  $observable: Observable<string>;
  takeCount: number;
}) {
  return {
    state: await new Promise((resolve) =>
      subscribeToRequestState($observable, (newState) => {
        if (takeCount-- < 1) {
          resolve(newState);
        }
      })
    ),
  };
}

describe('utils', () => {
  describe('subscribeToRequestState', () => {
    it('returns loading state', async () => {
      const { state } = await setup({ $observable: of(), takeCount: 0 });
      expect(state).toEqual({
        isLoading: true,
        isReady: false,
        hasFailed: false,
      });
    });

    it('returns ready state', async () => {
      const { state } = await setup({ $observable: of('ready'), takeCount: 1 });
      expect(state).toEqual({
        isLoading: false,
        isReady: true,
        hasFailed: false,
        value: 'ready',
      });
    });

    it('returns error state', async () => {
      const testError = new Error('test error');
      const { state } = await setup({
        $observable: throwError(() => testError),
        takeCount: 1,
      });
      expect(state).toEqual({
        isLoading: false,
        isReady: false,
        hasFailed: true,
        error: testError,
      });
    });
  });
});
