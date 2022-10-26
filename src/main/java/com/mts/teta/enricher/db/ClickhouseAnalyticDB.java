package com.mts.teta.enricher.db;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.teta.enricher.process.EnrichedMessage;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * Реализация, которая сохряняет полученные сообщения в Clickhouse.
 */
@Service
@RequiredArgsConstructor
public class ClickhouseAnalyticDB implements AnalyticDB {

  private final ClickhouseWrapper wrapper;
  private final ObjectMapper objectMapper;

  @Override
  public void persistMessage(EnrichedMessage enrichedMessage) {
    final var dataSource = wrapper.getDataSource();
    final var message = enrichedMessage.getMessage();
    try (final var connection = dataSource.getConnection()) {
      final var statement = connection.prepareStatement(""" 
          INSERT INTO db.event(user_id, event, element, app_name, app_id, event_params, server_timestamp, msisdn)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?)""");
      // в стандарте JDBC отчет параметров начинается с 1
      statement.setString(1, message.getUserId());
      statement.setString(2, message.getEvent());
      statement.setString(3, message.getElement());
      statement.setString(4, message.getAppName());
      statement.setLong(5, message.getAppId());
      statement.setString(
          6,
          objectMapper.writeValueAsString(message.getEventParams())
      );
      statement.setTimestamp(
          7,
          Timestamp.from(
              message.getTimestamp().toInstant()
          )
      );
      statement.setString(8, enrichedMessage.getMsisdn());
      statement.execute();
    } catch (SQLException e) {
      throw new AnalyticDBException(
          "Unexpected exception during connection to Clickhouse",
          e
      );
    } catch (JsonProcessingException e) {
      throw new AnalyticDBException(
          "Unexpected error during JSON serialization", e
      );
    }
  }

  @Configuration
  static class Config {

    @Bean
    @SneakyThrows
    public ClickhouseWrapper clickhouseWrapper(
        @Value("${clickhouse.url}") String url,
        @Value("${clickhouse.username}") String username,
        @Value("${clickhouse.password}") String password,
        @Value("${clickhouse.client-name}") String clientName
    ) {
      final var properties = new Properties();
      properties.setProperty("user", username);
      properties.setProperty("password", password);
      properties.setProperty("client_name", clientName);
      return new ClickhouseWrapper(
          new ClickHouseDataSource(
              url,
              properties
          )
      );
    }
  }
}
