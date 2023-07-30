const { createServer } = require("http");

const PORT = 8080;
const server = createServer();

server.on("request", (request, response) => {
  response.end(request.url);
});

exitOnSignal('SIGINT');
exitOnSignal('SIGTERM');

server.listen(PORT, () => {
  console.log(`starting server at port ${PORT}`);
});

function exitOnSignal(signal) {
  process.on(signal, function() {
    console.log('\ncaught ' + signal + ', exiting');
    process.exit(1);
  });
}
