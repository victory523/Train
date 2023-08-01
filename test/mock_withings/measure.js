const { getSearchParams } = require("./request");

function measure(request, response) {
  const authorization = request.headers.authorization;
  if (authorization !== "Bearer test-access-token") {
    console.log("access denied");
    response.writeHead(401);
    response.end();
    return;
  }

  const searchParams = getSearchParams(request);
  const startDate = searchParams.get("startdate");
  const endDate = searchParams.get("enddate");
  response.writeHead(200, {
    "Content-Type": "application/json",
  });
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

module.exports = {
  measure,
};
