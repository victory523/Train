const { createServer } = require("http");
const PORT = 8080;
const server = createServer();
const withLocalApps = !!process.env.WITH_LOCAL_APPS;
const entrypoint = process.env.DOCKER_NETWORK && !withLocalApps
  ? "http://reverse-proxy"
  : "http://localhost:9780";

console.log('Configuration:', { withLocalApps, entrypoint })

const routers = {
  client: {
    entryPoints: ["web"],
    service: "client",
    rule: "PathPrefix(`/`)",
  },
  servers: {
    entryPoints: ["web"],
    middlewares: ["authHeaders", "rewriteWithings", "rewriteStrava"],
    service: "server",
    rule: "PathPrefix(`/api`)",
  },
  mockBackupTool: {
    entryPoints: ["web"],
    service: "mockBackupTool",
    rule: "Path(`/db/last-backup-time`)",
  },
  mockWithings: {
    entryPoints: ["web"],
    service: "mockWithings",
    rule: "PathPrefix(`/withings`)",
  },
  mockStrava: {
    entryPoints: ["web"],
    service: "mockStrava",
    rule: "PathPrefix(`/strava`)",
  },
  chrome: {
    entryPoints: ["web"],
    service: "chrome",
    rule: "PathPrefix(`/chrome`)",
  },
};

const middlewares = {
  authHeaders: {
    headers: {
      customRequestHeaders: {
        "Remote-User": "rob",
        "Remote-Groups": "user",
        "Remote-Name": "Robert White",
        "Remote-Email": "robert.white@mockemail.com",
      },
    },
  },
  rewriteWithings: {
    plugin: {
      rewriteHeaders: {
        rewrites: [
          {
            header: "Location",
            regex: "^http://mock-withings:8080/(.+)$",
            replacement: `${entrypoint}/$1`,
          },
        ],
      },
    },
  },
  rewriteStrava: {
    plugin: {
      rewriteHeaders: {
        rewrites: [
          {
            header: "Location",
            regex: "^http://mock-strava:8080/(.+)$",
            replacement: `${entrypoint}/$1`,
          },
        ],
      },
    },
  },
};

const services = {
  client: {
    loadBalancer: {
      servers: [
        {
          url: withLocalApps ? "http://app:4200" : "http://client:80",
        },
      ],
    },
  },
  server: {
    loadBalancer: {
      servers: [
        {
          url: withLocalApps ? "http://app:8080" : "http://server:8080",
        },
      ],
    },
  },
  mockBackupTool: {
    loadBalancer: {
      servers: [
        {
          url: "http://mock-backup-tool:8080",
        },
      ],
    },
  },
  mockWithings: {
    loadBalancer: {
      servers: [
        {
          url: "http://mock-withings:8080",
        },
      ],
    },
  },
  mockStrava: {
    loadBalancer: {
      servers: [
        {
          url: "http://mock-strava:8080",
        },
      ],
    },
  },
  chrome: {
    loadBalancer: {
      servers: [
        {
          url: "http://chrome:4444",
        },
      ],
    },
  },
};

server.on("request", async (request, response) => {
  response.end(
    JSON.stringify({
      http: {
        routers,
        middlewares,
        services,
      },
    })
  );
});

process.on("SIGINT", () => server.close(() => process.exit()));
process.on("SIGTERM", () => server.close(() => process.exit()));

server.listen(PORT, () => {
  console.log(`starting server on port ${PORT}`);
});
