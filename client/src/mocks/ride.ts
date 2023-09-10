import { rest } from 'msw';
import { RideStats } from 'src/app/services/ride.service';

const mocks: Record<number, RideStats> = {
  0: {
    calories: 646 * 200,
    elevationGain: 408 * 200,
    distance: 11747.7 * 200,
    time: 3074 * 200,
  },
  1: { calories: 646, elevationGain: 408, distance: 11747.7, time: 3074 },
  7: {
    calories: 646 * 4,
    elevationGain: 408 * 4,
    distance: 11747.7 * 4,
    time: 3074 * 4,
  },
  30: {
    calories: 646 * 19,
    elevationGain: 408 * 19,
    distance: 11747.7 * 19,
    time: 3074 * 19,
  },
  365: {
    calories: 646 * 135,
    elevationGain: 408 * 135,
    distance: 11747.7 * 135,
    time: 3074 * 135,
  },
};

export const rideMocks = [
  rest.get('/api/ride/stats', (req, res, ctx) => {
    const period = parseInt(req.url.searchParams.get('period') ?? '0');

    return res(ctx.json(mocks[period]));
  }),
];
