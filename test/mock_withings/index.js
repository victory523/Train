const { createServer } = require("http");

const PORT = 8080;
const server = createServer();

server.on("request", async (request, response) => {
  console.log(request.url);
  const searchParams = new URL(request.url, `http://${request.headers.host}`)
    .searchParams;

  if (request.url.startsWith("/withings/oauth2_user/authorize2")) {
    const responseType = searchParams.get("response_type");
    const scope = searchParams.get("scope");
    const state = searchParams.get("state");
    const redirectURI = searchParams.get("redirect_uri");

    if (responseType !== "code" || scope !== "user.metrics") {
      console.log("validation error");
      response.writeHead(500);
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
  } else if (request.url.startsWith("/v2/oauth2")) {
    const body = new URLSearchParams(`?${await readBody(request)}`);
    const grantType = body.get("grant_type");
    const code = body.get("code");
    const action = body.get("action");
    const clientId = body.get("client_id");
    const clientSecret = body.get("client_secret");
    console.log({ grantType, code, action, clientId, clientSecret });

    if (
      (grantType !== "refresh_token" && grantType !== "authorization_code") ||
      (grantType === "authorization_code" && code !== "authorization-code") ||
      action !== "requesttoken" ||
      clientId !== "withings-client-id" ||
      clientSecret !== "withings-client-secret"
    ) {
      console.log("validation error");
      response.writeHead(500);
      response.end();
      return;
    }
    response.setHeader("Content-Type", "application/json");
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
    const startDate = searchParams.get("startdate");
    const endDate = searchParams.get("enddate");
    response.setHeader("Content-Type", "application/json");
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
              date: startDate,
              created: startDate,
              modified: endDate,
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

async function readBody(request) {
  const chunks = [];
  return new Promise((resolve) => {
    request.on("data", (chunk) => chunks.push(chunk));
    request.on("end", () => resolve(Buffer.concat(chunks).toString()));
  });
}

process.on("SIGINT", () => server.close(() => process.exit()));
process.on("SIGTERM", () => server.close(() => process.exit()));

server.listen(PORT, () => {
  console.log(`starting server at port ${PORT}`);
});
