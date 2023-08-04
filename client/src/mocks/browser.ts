import { rest, setupWorker } from 'msw';
import { WeightMeasurement } from 'src/app/weight.service';

function generateRandomWeightMeasurements(startDate: Date, endDate: Date) {
  const weightMeasurements: WeightMeasurement[] = [];

  const currentDate = new Date(startDate);
  while (currentDate <= endDate) {
    // 30% chance of having a weight measurement on a given day
    if (Math.random() < 0.3) {
      const weight = (Math.random() * (100 - 50) + 50).toFixed(2); // Random weight between 50 and 100 kg
      weightMeasurements.push({
        date: new Date(currentDate),
        weight: parseFloat(weight),
      });
    }

    currentDate.setDate(currentDate.getDate() + 1);
  }

  return weightMeasurements;
}

const startDate = new Date('2023-01-01');
const endDate = new Date('2023-12-31');

const randomWeightMeasurements = generateRandomWeightMeasurements(
  startDate,
  endDate
);

export const mocks = [
  rest.get('/db/last-backup-time', (_req, res, ctx) => {
    return res(ctx.status(200), ctx.json(new Date(Date.now() - 5 * 60 * 1000)));
  }),
  rest.post('/api/withings/sync', (_req, res, ctx) => {
    return res(ctx.status(200));
  }),
  rest.get('/api/weight', (req, res, ctx) =>
    res(ctx.json(randomWeightMeasurements))
  ),
];

const worker = setupWorker(...mocks);
worker.start({
  onUnhandledRequest: 'bypass',
});

export { rest, worker };
