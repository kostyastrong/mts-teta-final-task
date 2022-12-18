package com.mts.teta.kafka;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.mts.teta.enricher.db.AnalyticDB;
import com.mts.teta.enricher.process.EnrichedMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;

import java.util.Map;


@Configuration
public class KafkaConfiguration {
    @Autowired
    AnalyticDB analyticDB;

    @KafkaListener(topics = ENRICHED_TOPIC, groupId = "enriched_topic_group")
    public void onNewMessage(EnrichedMessage message) {
        analyticDB.persistMessage(message);
    }
    public static final String ENRICHED_TOPIC = "enriched_topic";

    @Bean
    public NewTopic enrichedTopic() {
        return new NewTopic(ENRICHED_TOPIC, 1, (short) 1);
    }

    @Bean
    JsonMessageConverter jsonMessageConverter() {
        return new JsonMessageConverter();
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate (ProducerFactory<Object, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory,
                Map.of( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class));
    }
}
