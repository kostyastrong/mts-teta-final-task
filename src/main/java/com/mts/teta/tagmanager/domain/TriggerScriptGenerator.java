package com.mts.teta.tagmanager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.teta.enricher.cache.UserInfoRepository;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.EnumUtils;

public class TriggerScriptGenerator {
    private String codeTemplate = """
            // дополнительно оборачивание в function - хак, который позволяет
            // выполнить код сразу при загрузке страницы
            (function() {
              console.log("Trigger {triggerName} is activated");
              {primaryFunction} {
                  console.log("Trigger {triggerName} is performing the action");
                  // здесь отправляется сообщение на бэкенд
                  fetch('{requestUrl}', {
                      method: 'POST',
                      mode: 'no-cors',
                      headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                      },
                      // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
                      body: JSON.stringify({
                          "userId": "{userId}",
                          event": "{eventType}",
                          "element": null, // setInterval не привязан к какому-то конкретному элементу на странице
                          // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                          "app_name": "{appName}",
                          "app_id": {appId},
                          // в event_params как раз сохраняет trigger.attributes
                          "event_params": {attributes}
                      })
                  })
              }, {delayMillis})
            })()
                        """;

    private void validateTrigger(Trigger trigger) {
        if (!EnumUtils.isValidEnum(Trigger.TriggerType.class, trigger.getType().toString())) {
            // Если будете добавлять новые типы триггеров, поддержку для них вам нужно будет
            // реализовать самостоятельно
            throw new UnsupportedOperationException(
                    "Указанный тип триггера еще не поддерживается: " + trigger.getType()
            );
        }
    }

    public String getTriggerPrimaryFunction(Trigger trigger) {
        return switch (trigger.getType()) {
            case SET_INTERVAL -> "setInterval(function()";
            case CLICK -> "document.addEventListener('click', function()";
            case SCROLL -> "document.addEventListener('scroll', function()";
            default -> throw new UnsupportedOperationException(
                    "Указанный тип триггера есть в enum, но еще не поддерживается: " + trigger.getType()
            );
        };
    }

    public String getTriggerEventType(Trigger trigger) {
        return switch (trigger.getType()) {
            case SET_INTERVAL -> "set_interval";
            case CLICK -> "click";
            case SCROLL -> "scroll";
            default -> "default_type";
        };
    }

    public TriggerScriptGenerator(Trigger trigger, UserInfoRepository userInfoRepository, ObjectMapper objectMapper) throws JsonProcessingException {
        this.validateTrigger(trigger);
        final var attributes = trigger.getAttributes().getSetTimeout();
        final var userIds = userInfoRepository.findAllUserIds();

        // request settings
        final String serverUrl = "http://localhost:8080/";
        final String apiMethodName = "api/message";
        final String requestUrl = serverUrl + apiMethodName;

        String primaryFunction = getTriggerPrimaryFunction(trigger);
        String eventType = getTriggerEventType(trigger);

        this.codeTemplate = this.codeTemplate
                .replaceAll("\\{triggerName}", trigger.getName())
                .replaceAll("\\{requestUrl}", requestUrl)
                .replaceAll(
                        "\\{attributes}",
                        // Здесь мы преобразуем Map<String, Object> в JSON, который и подставится в JSON.stringify
                        objectMapper.writeValueAsString(
                                attributes.getMessageToSend()
                        )
                )
                .replaceAll("\\{delayMillis}", String.valueOf(attributes.getDelayMillis()))
                .replaceAll("\\{appName}", trigger.getContainer().getApp().getName())
                .replaceAll("\\{appId}", String.valueOf(trigger.getContainer().getApp().getId()))
                .replaceAll(
                        "\\{userId}",
                        // Здесь простая реализация: подставляем случайный userId из всех существующих.
                        // Вы можете доработать и придумать что-то более интеллектуальное.
                        // Например, определенные userId будут относиться к определенным приложениям и триггер будет выбирать
                        // значение из соответствующего множества
                        userIds.get(ThreadLocalRandom.current().nextInt(userIds.size()))
                )
                .replaceAll("\\{primaryFunction}", primaryFunction)
                .replaceAll("\\{eventType}", eventType);
    }

    public String getJavaScript() {
        return codeTemplate;
    }
}
