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
    "test-db": {
      image: "postgres:15.2-bullseye",
      environment: {
        POSTGRES_DB: "training-log",
        POSTGRES_PASSWORD: "postgres",
        POSTGRES_USER: "postgres",
      },
      ports: ["5434:5432"],
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
        WITHINGS_ACCOUNTS_URI: `http://${
          dockerNetwork ? "reverse-proxy" : "localhost:8080"
        }/withings`,
        WITHINGS_API_URI: "http://mock-withings:8080",
        WITHINGS_CLIENT_ID: "withings-client-id",
        WITHINGS_CLIENT_SECRET: "withings-client-secret",
      },
      healthcheck: {
        test: ["CMD", "health-check"],
        interval: "10s",
        timeout: "30s",
        retries: 5,
        start_period: "15s",
      },
    },
    "reverse-proxy": {
      image: "traefik",
      ports: ["8080:80"],
      volumes: [
        `${workspaceRoot}/test/reverse_proxy/traefik_static_conf.yml:/etc/traefik/traefik.yml`,
        `${workspaceRoot}/test/reverse_proxy/traefik_dynamic_conf.yml:/etc/traefik/traefik_dynamic_conf.yml`,
      ],
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
