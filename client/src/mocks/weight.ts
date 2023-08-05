import { rest } from 'msw';
import { weightMeasurements } from './weightMeasurements';

export const weightMocks = [
  rest.get('/api/weight', (req, res, ctx) => {
    const period = parseInt(req.url.searchParams.get('period') ?? '7');
    const today = weightMeasurements.slice(-1)[0].date;
    const cutDate = today.getTime() - period * 1000 * 60 * 60 * 24;

    console.log({
      params: req.params,
      today,
      cutDate: new Date(cutDate),
      period: period * 1000 * 60 * 60 * 24,
    });

    return res(
      ctx.json(
        weightMeasurements.filter(
          ({ date }) => period === 0 || date.getTime() >= cutDate
        )
      )
    );
  }),
];
