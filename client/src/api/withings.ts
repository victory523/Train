import { fetchJSON } from "./core";

export type WeightResponse = {
    weight?: number;
}

export function fetchWeight() {
    return fetchJSON<WeightResponse>('/api/withings/weight');
}