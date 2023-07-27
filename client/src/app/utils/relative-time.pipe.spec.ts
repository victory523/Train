import { RelativeTimePipe } from './relative-time.pipe';

describe('RelativeTimePipe', () => {
  [
    { date: new Date(), result: 'now' },
    { date: new Date(Date.now() - 999), result: '1 second ago' },
    { date: new Date(Date.now() - 1000), result: '1 second ago' },
    { date: new Date(Date.now() - 59 * 1000), result: '59 seconds ago' },
    { date: new Date(Date.now() - 60 * 1000), result: '1 minute ago' },
    { date: new Date(Date.now() - 59 * 60 * 1000), result: '59 minutes ago' },
    { date: new Date(Date.now() - 60 * 60 * 1000), result: '1 hour ago' },
    { date: new Date(Date.now() - 23 * 60 * 60 * 1000), result: '23 hours ago' },
    { date: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000), result: 'yesterday' },
    { date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), result: '2 days ago' },
    { date: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000), result: '6 days ago' },
    { date: new Date(Date.now() - 1 * 7 * 24 * 60 * 60 * 1000), result: 'last week' },
    { date: new Date(Date.now() - 4 * 7 * 24 * 60 * 60 * 1000), result: '4 weeks ago' },
    { date: new Date(Date.now() - 29 * 24 * 60 * 60 * 1000), result: '4 weeks ago' },
    { date: new Date(Date.now() - 1 * 30 * 24 * 60 * 60 * 1000), result: 'last month' },
    { date: new Date(Date.now() - 12 * 30 * 24 * 60 * 60 * 1000), result: '12 months ago' },
    { date: new Date(Date.now() - 364 * 24 * 60 * 60 * 1000), result: '12 months ago' },
    { date: new Date(Date.now() - 1 * 365 * 24 * 60 * 60 * 1000), result: 'last year' },
    { date: new Date(Date.now() - 5 * 365 * 24 * 60 * 60 * 1000), result: '5 years ago' },
  ].forEach(({ date, result }) =>
    it(`formats ${date} as ${result}`, () => {
      const pipe = new RelativeTimePipe();
      expect(pipe.transform(date)).toBe(result);
    })
  );
});
