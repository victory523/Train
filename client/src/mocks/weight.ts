import { HttpResponse, http } from 'msw';
import { weightMeasurements } from './weightMeasurements';

export const weightMocks = [
  http.get('/api/weight', ({ request }) => {
    const url = new URL(request.url);
    const period = parseInt(url.searchParams.get('period') ?? '0');
    const today = weightMeasurements.slice(-1)[0].date;
    const cutDate = today.getTime() - period * 1000 * 60 * 60 * 24;

    return HttpResponse.json(
      period
        ? weightMeasurements.filter(
            ({ date }) => period === 0 || date.getTime() >= cutDate
          )
        : weightMeasurements
    );
  }),
];
