import { rest } from 'msw';
import { weightMeasurements } from './weightMeasurements';

export const weightMocks = [
  rest.get('/api/weight', (req, res, ctx) => {
    const period = parseInt(req.url.searchParams.get('period') ?? '0');
    const today = weightMeasurements.slice(-1)[0].date;
    const cutDate = today.getTime() - period * 1000 * 60 * 60 * 24;

    if (!period) {
      return res(ctx.json(weightMeasurements));
    }

    return res(
      ctx.json(
        weightMeasurements.filter(
          ({ date }) => period === 0 || date.getTime() >= cutDate
        )
      )
    );
  }),
];
