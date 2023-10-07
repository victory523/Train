import { rest, setupWorker } from 'msw';
import { weightMocks } from './weight';
import { rideMocks } from './ride';

export const mocks = [
  rest.get('/db/last-backup-time', (_req, res, ctx) => {
    return res(ctx.status(200), ctx.json(new Date(Date.now() - 5 * 60 * 1000)));
  }),
  rest.post('/api/withings/sync', (_req, res) => {
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
    return res();
  }),
  rest.post('/api/strava/activities/sync', (_req, res) => {
    return res();
  }),
  ...rideMocks,
  ...weightMocks
];

const worker = setupWorker(...mocks);
worker.start({
  onUnhandledRequest: 'bypass',
});

export { rest, worker };
