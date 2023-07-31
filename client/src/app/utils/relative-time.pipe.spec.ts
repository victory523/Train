import { RelativeTimePipe } from './relative-time.pipe';

describe('RelativeTimePipe', () => {
  [
    { getDate: () => new Date(), result: 'now' },
    {
      getDate: () => new Date(Date.now() - 999),
      result: '1 second ago',
    },
    {
      getDate: () => new Date(Date.now() - 1000),
      result: '1 second ago',
    },
    {
      getDate: () => new Date(Date.now() - 59 * 1000),
      result: '59 seconds ago',
    },
    { getDate: () => new Date(Date.now() - 60 * 1000), result: '1 minute ago' },
    {
      getDate: () => new Date(Date.now() - 59 * 60 * 1000),
      result: '59 minutes ago',
    },
    {
      getDate: () => new Date(Date.now() - 60 * 60 * 1000),
      result: '1 hour ago',
    },
    {
      getDate: () => new Date(Date.now() - 23 * 60 * 60 * 1000),
      result: '23 hours ago',
    },
    {
      getDate: () => new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
      result: 'yesterday',
    },
    {
      getDate: () => new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
      result: '2 days ago',
    },
    {
      getDate: () => new Date(Date.now() - 6 * 24 * 60 * 60 * 1000),
      result: '6 days ago',
    },
    {
      getDate: () => new Date(Date.now() - 1 * 7 * 24 * 60 * 60 * 1000),
      result: 'last week',
    },
    {
      getDate: () => new Date(Date.now() - 4 * 7 * 24 * 60 * 60 * 1000),
      result: '4 weeks ago',
    },
    {
      getDate: () => new Date(Date.now() - 29 * 24 * 60 * 60 * 1000),
      result: '4 weeks ago',
    },
    {
      getDate: () => new Date(Date.now() - 1 * 30 * 24 * 60 * 60 * 1000),
      result: 'last month',
    },
    {
      getDate: () => new Date(Date.now() - 12 * 30 * 24 * 60 * 60 * 1000),
      result: '12 months ago',
    },
    {
      getDate: () => new Date(Date.now() - 364 * 24 * 60 * 60 * 1000),
      result: '12 months ago',
    },
    {
      getDate: () => new Date(Date.now() - 1 * 365 * 24 * 60 * 60 * 1000),
      result: 'last year',
    },
    {
      getDate: () => new Date(Date.now() - 5 * 365 * 24 * 60 * 60 * 1000),
      result: '5 years ago',
    },
  ].forEach(({ getDate, result }) =>
    it(`formats ${result}`, () => {
      const pipe = new RelativeTimePipe();
      expect(pipe.transform(getDate())).toBe(result);
    })
  );
});
