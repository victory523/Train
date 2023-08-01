import { rest, setupWorker } from 'msw';
import { WeightResponse } from 'src/app/weight.service';

export const mocks = [
  rest.get('/db/last-backup-time', (_req, res, ctx) => {
    return res(ctx.status(200), ctx.json(new Date(Date.now() - 5 * 60 * 1000)));
  }),
  rest.post('/api/withings/sync', (_req, res, ctx) => {
    return res(ctx.status(200));
  }),
  rest.get('/api/weight', (_req, res, ctx) => {
    return res(ctx.status(200), ctx.json({ weight: 87.63 } as WeightResponse));
  }),
];

const worker = setupWorker(...mocks);
worker.start({
  onUnhandledRequest: 'bypass'
});

export { worker, rest };
