import { http, HttpResponse } from 'msw';
import { setupWorker } from 'msw/browser';
import { rideMocks } from './ride';
import { weightMocks } from './weight';
import { authMocks } from './auth';

export const mocks = [
  ...authMocks,
  http.get('/db/last-backup-time', () =>
    HttpResponse.json(new Date(Date.now() - 5 * 60 * 1000))
  ),
  http.post('/api/withings/sync', () => {
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
    return new HttpResponse();
  }),
  http.post('/api/strava/activities/sync', () => new HttpResponse()),
  ...rideMocks,
  ...weightMocks,
];

const worker = setupWorker(...mocks);
worker.start({
  onUnhandledRequest: 'bypass',
});
