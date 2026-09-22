package com.davidantasdev.nomismavault;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.PostgreSQLContainer;

@EnabledIfSystemProperty(named = "runPostgresIT", matches = "true")
class PostgreSqlFlywayTest {
  @Test
  void appliesAllFlywayMigrationsToPostgreSql() {
    try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")) {
      postgres.start();
      int migrations =
          Flyway.configure()
              .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
              .locations("classpath:db/migration")
              .load()
              .migrate()
              .migrationsExecuted;
      assertEquals(2, migrations);
    }
  }
}
