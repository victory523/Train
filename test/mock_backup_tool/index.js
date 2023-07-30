const { createServer } = require("http");

const PORT = 8080;
const server = createServer();

server.on("request", (request, response) => {
  response.setHeader("Content-Type", "application/json");
  response.end(JSON.stringify(new Date(Date.now() - 5 * 60 * 1000)));
});

process.on("SIGINT", () => server.close(() => process.exit()));
process.on("SIGTERM", () => server.close(() => process.exit()));

server.listen(PORT, () => {
  console.log(`starting server at port ${PORT}`);
});
