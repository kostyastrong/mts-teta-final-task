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
                        
              var elementName = null;
              {beforePrimaryFunction}
              
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
                          "event": "{triggerName}",
                          "element": elementName, // setInterval не привязан к какому-то конкретному элементу на странице
                          // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                          "app_name": "{appName}",
                          "app_id": {appId},
                          // в event_params как раз сохраняет trigger.attributes
                          "event_params": {attributes}
                      })
                  })
              }, {delayMillis})
              {afterPrimaryFunction}
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

    public String getPrimaryFunction(Trigger trigger) {
        return switch (trigger.getType()) {
            case SET_INTERVAL -> "setInterval(function()";
            case CLICK -> "document.addEventListener('click', function()";
            case SCROLL -> "document.addEventListener('scroll', function()";
            case BUTTON_CLICK -> "button.addEventListener('click', function()";
            default -> throw new UnsupportedOperationException(
                    "Указанный тип триггера есть в enum, но еще не поддерживается: " + trigger.getType()
            );
        };
    }

    public String getBeforePrimaryFunction(Trigger trigger) {
        if (trigger.getType().equals(Trigger.TriggerType.BUTTON_CLICK)) {
            return """
                    var buttons = document.querySelectorAll("button");
                    for(var i = 0, len = buttons.length; i < len; i++) {
                      button = buttons[i];
                                      """;
        }
        return "";
    }

    public String getAfterPrimaryFunction(Trigger trigger) {
        if (trigger.getType().equals(Trigger.TriggerType.BUTTON_CLICK)) {
            return "}";
        }
        return "";
    }

    public TriggerScriptGenerator(Trigger trigger, UserInfoRepository userInfoRepository, ObjectMapper objectMapper) throws JsonProcessingException {
        this.validateTrigger(trigger);
        final var attributes = trigger.getAttributes().getSetTimeout();
        final var userIds = userInfoRepository.findAllUserIds();

        // request settings
        final String serverUrl = "http://localhost:8080/";
        final String apiMethodName = "api/message";
        final String requestUrl = serverUrl + apiMethodName;

        String primaryFunction = getPrimaryFunction(trigger);
        String beforePrimaryFunction = getBeforePrimaryFunction(trigger);
        String afterPrimaryFunction = getAfterPrimaryFunction(trigger);

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
                .replaceAll("\\{beforePrimaryFunction}", beforePrimaryFunction)
                .replaceAll("\\{primaryFunction}", primaryFunction)
                .replaceAll("\\{afterPrimaryFunction}", afterPrimaryFunction);
    }

    public String getJavaScript() {
        return codeTemplate;
    }
}
