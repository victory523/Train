import { rest } from "msw";
import { weightMeasurements } from "./weightMeasurements";

export const weightMocks = [
  rest.get('/api/weight', (req, res, ctx) =>
    res(ctx.json(weightMeasurements))
  ),
]
