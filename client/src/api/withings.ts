import { fetchJSON } from "./core";

export function fetchWeight() {
    return fetchJSON<number>('/api/withings/weight');
}