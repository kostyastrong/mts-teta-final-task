package com.mts.teta.enricher;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Здесь мы парсим поля, которые нам отправил TagManager,
 * с помощью javascript, который вернул ContainerController.
 *
 * Любое поле может отсутствовать. Тогда мы не должны падать, а просто заполняем его как null
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Message {
  private  String userId;
  private  String event;
  private  String element;
  private  String appName;
  private String appId;
  // время, когда сообщение было получено на сервере
  // Хорошо бы еще фиксировать время, когда клиент его отправил.
  // Для этого вам нужно будет внести изменения в JS, который возвращает ContainerController
  private  OffsetDateTime timestamp;
  private  Map<String, Object> eventParams;
  @JsonCreator

  public Message(Map<String, Object> rawMessage) {
    this.userId = parseString(rawMessage, "userId");
    this.event = parseString(rawMessage, "event");
    this.element = parseString(rawMessage, "element");
    this.appName = parseString(rawMessage, "appName");
    this.appId = parseString(rawMessage, "appId");
    this.eventParams = parseMap(rawMessage, "eventParams");
    this.timestamp = OffsetDateTime.now();
  }

  private static String parseString(Map<String, Object> msg, String field) {
     var value = msg.get(field);
    if (value instanceof String str) {
      return str;
    }
    return "";
  }

  // Алгоритм здесь довольно примитивный. А что, если нам передадут число в виде строки?
  // Можно улучшить алгоритм :)
  private static Long parseLong(Map<String, Object> msg, String field) {
     var value = msg.get(field);
    if (value instanceof Integer intValue) {
      return intValue.longValue();
    }
    if (value instanceof Long longValue) {
      return longValue;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> parseMap(Map<String, Object> msg, String field) {
     var value = msg.get(field);
    if (value instanceof Map map) {
      return map;
    }
    return Collections.emptyMap();
  }
}
