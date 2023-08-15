const { readFileSync, writeFileSync } = require("fs");
const { resolve } = require("path");

const entries = readFileSync(resolve(__dirname, "real.log"), "utf8")
  .split("\n")
  .map((entry) => entry.split("\t"))
  .map(([date, value]) => ({ date, value }))
  .filter(({ value }) => value)
  .map(({ date, value }) => ({
    date: (new Date('2021-08-07T00:00:00.000Z').getTime() - new Date(date).getTime()) / (1000 * 3600 * 24),
    weight: parseFloat(value),
  }));

writeFileSync(resolve(__dirname, "real.json"), JSON.stringify(entries), 'utf8');

console.log(entries);
