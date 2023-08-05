const { readFileSync, writeFileSync } = require("fs");
const { resolve } = require("path");

const entries = readFileSync(resolve(__dirname, "real.log"), "utf8")
  .split("\n")
  .map((entry) => entry.split("\t"))
  .map(([date, value]) => ({ date, value }))
  .filter(({ value }) => value)
  .map(({ date, value }) => ({
    date: new Date(date),
    weight: parseFloat(value),
  }));

writeFileSync(resolve(__dirname, "real.json"), JSON.stringify(entries), 'utf8');

console.log(entries);
