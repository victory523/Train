import { rest, setupWorker } from 'msw';
import { weightMocks } from './weight';

export const mocks = [
  rest.get('/db/last-backup-time', (_req, res, ctx) => {
    return res(ctx.status(200), ctx.json(new Date(Date.now() - 5 * 60 * 1000)));
  }),
  rest.post('/api/withings/sync', (_req, res, ctx) => {
    // return res(
    //   ctx.status(401),
    //   ctx.json({
    //     _links: {
    //       oauth2Login: {
    //         href: 'http://localhost:3000/api/withings/authorize',
    //       },
    //     },
    //   })
    // );
    return res(ctx.status(200));
  }),
  ...weightMocks
];

const worker = setupWorker(...mocks);
worker.start({
  onUnhandledRequest: 'bypass',
});

export { rest, worker };
