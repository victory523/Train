const { createServer } = require("http");

const PORT = 8080;
const server = createServer();

server.on("request", (request, response) => {
  console.log(request.url);
  const searchParams = new URL(request.url, `http://${request.headers.host}`)
    .searchParams;

  if (request.url.startsWith("/withings/oauth2_user/authorize2")) {
    const responseType = searchParams.get("response_type");
    const scope = searchParams.get("scope");
    const state = searchParams.get("state");
    const redirectURI = searchParams.get("redirect_uri");

    if (responseType !== "code" || scope !== "user.metrics") {
      response.statusCode(500);
      response.end();
      return;
    }

    const location = new URL(redirectURI);
    location.searchParams.append("state", state);
    location.searchParams.append("code", "authorization-code");

    response.writeHead(302, {
      Location: location,
    });
    return response.end();
  } else if (request.url.startsWith("/withings/v2/oauth2")) {
    return response.end(
      JSON.stringify({
        status: 0,
        body: {
          userid: "363",
          access_token: "test-access-token",
          refresh_token: "test-refresh-token",
          expires_in: 10800,
          scope: "user.info,user.metrics",
          csrf_token: "PACnnxwHTaBQOzF7bQqwFUUotIuvtzSM",
          token_type: "Bearer",
        },
      })
    );
  } else if (request.url.startsWith("/measure")) {
    return response.end(
      JSON.stringify({
        status: 0,
        body: {
          updatetime: "string",
          timezone: "string",
          measuregrps: [
            {
              grpid: 12,
              attrib: 1,
              date: 1594245600,
              created: 1594246600,
              modified: 1594257200,
              category: 1594257200,
              deviceid: "892359876fd8805ac45bab078c4828692f0276b1",
              measures: [
                {
                  value: 65750,
                  type: 1,
                  unit: -3,
                  algo: 3425,
                  fm: 1,
                  fw: 1000,
                },
              ],
              comment: "A measurement comment",
              timezone: "Europe/Paris",
            },
          ],
          more: 0,
          offset: 0,
        },
      })
    );
  }

  response.end(request.url);
});

process.on("SIGINT", () => server.close(() => process.exit()));
process.on("SIGTERM", () => server.close(() => process.exit()));

server.listen(PORT, () => {
  console.log(`starting server at port ${PORT}`);
});
