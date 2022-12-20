package com.mts.teta.enricher.db;


import com.clickhouse.jdbc.ClickHouseDataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DBInitializer {

  private final ClickhouseWrapper wrapper;

  // Запускается при старте приложения.
  // Создает в Clickhouse таблицу, если ее там уже нет
  @EventListener(ApplicationStartedEvent.class)
  @SneakyThrows
  public void createTableIfNotExists() {
    final var dataSource = wrapper.getDataSource();
    try (final var connection = dataSource.getConnection()) {
      final var statement = connection.createStatement();
      statement.execute(
          """
              CREATE TABLE IF NOT EXISTS db.event 
              (
                user_id TEXT,
                event TEXT,
                element TEXT,
                app_name TEXT,
                app_id TEXT,
                event_params TEXT,
                server_timestamp Datetime,
                msisdn TEXT
              ) ENGINE = TinyLog
              """
      );
    }
  }
}
