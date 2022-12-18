package com.mts.teta.kafka;

import com.clickhouse.client.internal.google.gson.Gson;
import com.clickhouse.client.internal.google.gson.JsonSyntaxException;
import com.mts.teta.enricher.process.EnrichedMessage;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class EnrichedMessageConverter {
    public String serialize(EnrichedMessage message) {
        Gson gson = new Gson();
        return gson.toJson(message);
    }

    public EnrichedMessage deSerialize(String message) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(message, EnrichedMessage.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Json error converting to message class");
            throw new RuntimeException(e);
        }
    }
}
