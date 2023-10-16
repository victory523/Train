const os = require("os");
const arch = os.arch();
const dockerNetwork = process.env.DOCKER_NETWORK;
const workspaceRoot = process.env.WORKSPACE_ROOT ?? "..";

const config = {
  version: "3.8",
  name: "training-log-pro_test",
  services: {
    "mock-backup-tool": {
      build: "./mock_backup_tool",
    },
    "mock-withings": {
      build: "./mock_withings",
    },
    "mock-strava": {
      build: "./mock_strava",
    },
    "test-db": {
      image: "postgres:15.2-bullseye",
      environment: {
        POSTGRES_DB: "training-log",
        POSTGRES_PASSWORD: "postgres",
        POSTGRES_USER: "postgres",
      },
      ports: ["9734:5432"],
    },
    client: {
      image: "mucsi96/training-log-pro-client",
      healthcheck: {
        test: ["CMD", "health-check"],
        interval: "10s",
        timeout: "30s",
        retries: 5,
        start_period: "1s",
      },
    },
    server: {
      image: "mucsi96/training-log-pro-server",
      environment: {
        POSTGRES_HOSTNAME: "test-db",
        POSTGRES_PORT: 5432,
        POSTGRES_DB: "training-log",
        POSTGRES_PASSWORD: "postgres",
        POSTGRES_USER: "postgres",
        SPRING_ACTUATOR_PORT: 8082,
        SPRING_ADMIN_SERVER_HOST: "localhost",
        SPRING_ADMIN_SERVER_PORT: 9090,
        WEBDRIVER_API_URI: "http://chrome:4444/wd/hub",
        WITHINGS_ACCOUNTS_URI: `http://${
          dockerNetwork ? "reverse-proxy" : "localhost:9780"
        }/withings`,
        WITHINGS_API_URI: "http://mock-withings:8080",
        WITHINGS_CLIENT_ID: "withings-client-id",
        WITHINGS_CLIENT_SECRET: "withings-client-secret",
        STRAVA_API_URI: "http://mock-strava:8080",
        STRAVA_CLIENT_ID: "strava-client-id",
        STRAVA_CLIENT_SECRET: "strava-client-secret",
      },
      healthcheck: {
        test: ["CMD", "health-check"],
        interval: "10s",
        timeout: "30s",
        retries: 5,
        start_period: "1s",
      },
      depends_on: {
        chrome: {
          condition: "service_healthy",
        },
      },
    },
    "reverse-proxy": {
      image: "traefik",
      ports: ["9780:80"],
      volumes: [
        `${workspaceRoot}/test/reverse_proxy/traefik_static_conf.yml:/etc/traefik/traefik.yml`,
        `${workspaceRoot}/test/reverse_proxy/traefik_dynamic_conf.yml:/etc/traefik/traefik_dynamic_conf.yml`,
      ],
    },
    chrome: {
      image: `${
        arch === "arm64"
          ? "seleniarm/standalone-chromium"
          : "selenium/standalone-chrome"
      }:116.0-chromedriver-116.0-grid-4.10.0-20230828`,
      healthcheck: {
        test: [
          "CMD",
          "/opt/bin/check-grid.sh",
          "--host",
          "0.0.0.0",
          "--port",
          "4444",
        ],
        interval: "10s",
        timeout: "30s",
        retries: 5,
        start_period: "15s",
      },
    },
  },
  ...(dockerNetwork && {
    networks: {
      default: {
        name: dockerNetwork,
        external: true,
      },
    },
  }),
};

console.log(JSON.stringify(config));
