const { getBody } = require("./request");

async function get_access_token(request, response) {
  const body = new URLSearchParams(`?${await getBody(request)}`);
  const grantType = body.get("grant_type");
  const code = body.get("code");
  const clientId = body.get("client_id");
  const clientSecret = body.get("client_secret");
  console.log({ grantType, code, clientId, clientSecret });

  if (
    (grantType !== "refresh_token" && grantType !== "authorization_code") ||
    (grantType === "authorization_code" && code !== "authorization-code") ||
    clientId !== "strava-client-id" ||
    clientSecret !== "strava-client-secret"
  ) {
    console.log("validation error");
    response.writeHead(500);
    response.end();
    return;
  }

  response.writeHead(200, {
    "Content-Type": "application/json",
  });
  return response.end(
    JSON.stringify({
      token_type: "Bearer",
      expires_at: Date.now() / 1000 + 21600,
      expires_in: 21600,
      refresh_token: "test-refresh-token",
      access_token: "test-access-token",
      athlete: {
        id: 2323,
      },
    })
  );
}

module.exports = {
  get_access_token,
};
