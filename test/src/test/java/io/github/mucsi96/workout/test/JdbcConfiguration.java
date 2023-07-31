package io.github.mucsi96.workout.test;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcConfiguration {

  @Bean
  JdbcTemplate jdbcTemplate() {
    DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
    String host = System.getenv("DOCKER_NETWORK") != null ? "test-db:5432" : "localhost:5434";
    dataSourceBuilder.url(String.format("jdbc:postgresql://%s/training-log", host));
    dataSourceBuilder.username("postgres");
    dataSourceBuilder.password("postgres");
    return new JdbcTemplate(dataSourceBuilder.build());
  }

}
