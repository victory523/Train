package io.github.mucsi96.workout.test;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
  private final GenericContainer<?> client;
  private final GenericContainer<?> server;

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {

    return builder.routes()
        .route("server",
            r -> r
                .path("/api/**")
                .filters(f -> f
                    .addRequestHeader("Remote-User", "rob")
                    .addRequestHeader("Remote-Groups", "user")
                    .addRequestHeader("Remote-Name", "Robert White")
                    .addRequestHeader("Remote-Email", "robert.white@mockemail.com"))
                .uri("http://" + server.getHost() + ":" + server.getFirstMappedPort()))
        .route("client",
            r -> r
                .path("/**")
                .uri("http://" + client.getHost() + ":" + client.getFirstMappedPort()))
        .build();
  }
}
