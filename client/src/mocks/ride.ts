import { http, HttpResponse } from 'msw';
import { RideStats } from 'src/app/ride/ride.service';

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
  http.get('/api/ride/stats', ({ request }) => {
    const url = new URL(request.url);
    const period = parseInt(url.searchParams.get('period') ?? '0');

    return HttpResponse.json(mocks[period]);
  }),
];
