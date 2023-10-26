const { getSearchParams } = require("./request");

function authozire(request, response) {
  const searchParams = getSearchParams(request);
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

  response.writeHead(200, {
    "Content-Type": "text/html;charset=utf-8",
  });
  const responseBody = `
    <!DOCTYPE html>
    <h1>Mock Withings</h1>
    <a href="${location.toString()}">Authorize</a>
  `;
  console.log("Response: ", responseBody);
  return response.end(responseBody);
}

module.exports = {
  authozire,
};
