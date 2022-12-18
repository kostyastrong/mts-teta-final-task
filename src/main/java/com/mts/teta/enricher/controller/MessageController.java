package com.mts.teta.enricher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.teta.enricher.Message;
import com.mts.teta.enricher.db.AnalyticDB;
import com.mts.teta.enricher.kafka.KafkaTopicConfig;
import com.mts.teta.enricher.process.EnrichedMessage;
import com.mts.teta.enricher.process.EnricherService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequiredArgsConstructor
@Validated
public class MessageController {
  private final EnricherService enricherService;
  private final AnalyticDB analyticDB;
  private final ObjectMapper objectMapper;

  @Autowired
  private KafkaTemplate<String, EnrichedMessage> kafkaTemplate;

  public void sendMessage(EnrichedMessage message) {

    ListenableFuture<SendResult<String, EnrichedMessage>> future =
            kafkaTemplate.send(KafkaTopicConfig.topicName, message);


    future.addCallback(new ListenableFutureCallback<SendResult<String, EnrichedMessage>>() {

      @Override
      public void onSuccess(SendResult<String, EnrichedMessage> result) {
        System.out.println("Sent message=[" + message.getMsisdn() +
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
      }

      @Override
      public void onFailure(Throwable ex) {
        System.out.println("Unable to send message=["
                + message.getMsisdn() + "] due to : " + ex.getMessage());
      }
    });
  }

  @SuppressWarnings("unchecked")
  @SneakyThrows
  @PostMapping(value = "/api/message", consumes = TEXT_PLAIN_VALUE)
  // Важно, что здесь мы принимаем любой JSON, а не какой-то конкретный формат.
  // Потому что со временем формат сообщений может меняться, да и клиенты нашей платформы
  // могут отправлять нам не структированные сообщения. Мы все равно должны их все принимать и не отвечать ошибкой.
  public void acceptMessage(@NotNull @RequestBody String rawMessage) {
    final var message = new Message(objectMapper.readValue(rawMessage, Map.class));
    final var enrichedMessage = enricherService.enrich(message);
    analyticDB.persistMessage(enrichedMessage);
  }
}
