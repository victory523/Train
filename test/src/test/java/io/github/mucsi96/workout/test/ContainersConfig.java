package io.github.mucsi96.workout.test;

import java.time.Duration;
import java.util.function.Consumer;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

class DevContainerNetwork implements Network {

  @Override
  public String getId() {
    return System.getenv("DOCKER_NETWORK");
  }

  @Override
  public void close() {
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return null;
  }

};

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

  public static final DockerImageName CLIENT_IMAGE = DockerImageName.parse("mucsi96/training-log-pro-client:latest");
  public static final DockerImageName SERVER_IMAGE = DockerImageName.parse("mucsi96/training-log-pro-server:latest");

  Network network() {
    return System.getenv("DOCKER_NETWORK") != null ? new DevContainerNetwork() : null;
  }

  Consumer<OutputFrame> logConsumer(String prefix, String color) {
    return frame -> System.out.print(color + "[" + prefix + "] " + ConsoleColors.RESET + frame.getUtf8String());
  }

  @Bean
  PostgreSQLContainer<?> db() {
    return new PostgreSQLContainer<>("postgres:15.3-alpine3.18")
        .withLogConsumer(logConsumer("db", ConsoleColors.YELLOW))
        .withNetwork(network());
  }

  @Bean
  GenericContainer<?> client() {
    return new GenericContainer<>(CLIENT_IMAGE)
        .withLogConsumer(logConsumer("client", ConsoleColors.GREEN))
        .withExposedPorts(80)
        .withNetwork(network())
        .waitingFor(
            Wait.forHttp("/?startup").withStartupTimeout(Duration.ofSeconds(5)));
  }

  @Bean
  GenericContainer<?> server(PostgreSQLContainer<?> db) {
    return new GenericContainer<>(SERVER_IMAGE).withLogConsumer(logConsumer("server", ConsoleColors.BLUE))
        .withExposedPorts(8080)
        .withExposedPorts(8082)
        .withNetwork(network())
        .withEnv("SERVER_SERVLET_CONTEXT_PATH", "/api")
        .withEnv("SPRING_ACTUATOR_PORT", "8082")
        .withEnv("POSTGRES_DB", db.getDatabaseName())
        .withEnv("POSTGRES_HOSTNAME", db.getHost())
        .withEnv("POSTGRES_PORT", db.getFirstMappedPort().toString())
        .withEnv("POSTGRES_USER", db.getUsername())
        .withEnv("POSTGRES_PASSWORD", db.getPassword())
        .waitingFor(
            Wait.forHttp("/actuator/health/liveness").forPort(8082).withStartupTimeout(Duration.ofSeconds(40)));
  }
}
