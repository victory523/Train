async function getBody(request) {
  const chunks = [];
  return new Promise((resolve) => {
    request.on("data", (chunk) => chunks.push(chunk));
    request.on("end", () => resolve(Buffer.concat(chunks).toString()));
  });
}

function getSearchParams(request) {
  return new URL(request.url, `http://${request.headers.host}`).searchParams;
}

module.exports = {
  getBody,
  getSearchParams
};
