const { getBody } = require("./request");

async function get_access_token(request, response) {
  const body = new URLSearchParams(`?${await getBody(request)}`);
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

  response.writeHead(200, {
    "Content-Type": "application/json",
  });
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
}

module.exports = {
  get_access_token
}
