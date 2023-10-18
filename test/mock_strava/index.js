const { createServer } = require("http");
const { authozire } = require("./authorize");
const { get_access_token } = require("./get_access_token");
const { getActivities, getActivity } = require("./activities");

const PORT = 8080;
const server = createServer();

server.on("request", async (request, response) => {
  console.log(request.url);

  // https://developer.withings.com/api-reference/
  if (request.method === "GET" && request.url.startsWith("/strava/oauth/authorize")) {
    return authozire(request, response);
  } else if (
    request.method === "POST" &&
    request.url.startsWith("/strava/oauth/token")
  ) {
    return await get_access_token(request, response);
  } else if (
    request.method === "GET" &&
    request.url.startsWith("/strava/api/v3/athlete/activities")
  ) {
    return getActivities(request, response);
  } else if (
    request.method === "GET" &&
    request.url.startsWith("/strava/api/v3/activities/")
  ) {
    return getActivity(request, response);
  }

  response.end(request.url);
});

process.on("SIGINT", () => server.close(() => process.exit()));
process.on("SIGTERM", () => server.close(() => process.exit()));

server.listen(PORT, () => {
  console.log(`starting server at port ${PORT}`);
});
