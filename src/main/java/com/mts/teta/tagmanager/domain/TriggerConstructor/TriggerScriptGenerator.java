package com.mts.teta.tagmanager.domain.TriggerConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.teta.enricher.cache.UserInfoRepository;

import java.util.concurrent.ThreadLocalRandom;

import com.mts.teta.tagmanager.domain.Trigger;
import org.apache.commons.lang3.EnumUtils;

public class TriggerScriptGenerator {
    private String codeTemplate;
    private TriggerTargetType targetType;

    private void validateTrigger(Trigger trigger) {
        if (!EnumUtils.isValidEnum(Trigger.TriggerType.class, trigger.getType().toString())) {
            // Если будете добавлять новые типы триггеров, поддержку для них вам нужно будет
            // реализовать самостоятельно
            throw new UnsupportedOperationException(
                    "Указанный тип триггера еще не поддерживается: " + trigger.getType()
            );
        }
    }

    private void setCodeTemplate(Trigger trigger) {
        if (trigger.getType().equals(Trigger.TriggerType.BUTTON_CLICK)) {
            this.codeTemplate = TriggerScriptRepository.MultipleElementsTemplate;
            this.targetType = TriggerTargetType.MULTIPLE_TARGET;
        } else {
            this.codeTemplate = TriggerScriptRepository.SimpleTemplate;
            this.targetType = TriggerTargetType.SINGLE_TARGET;
        }
    }

    private String getPrimaryFunction(Trigger trigger) {
        return switch (trigger.getType()) {
            case SET_INTERVAL -> "setInterval";
            case CLICK, SCROLL -> "document.addEventListener";
            case BUTTON_CLICK -> "elements[i].addEventListener";
            default -> "";
        };
    }

    private String getBeforePrimaryFunction(Trigger trigger) {
        return "";
    }

    private String getAfterPrimaryFunction(Trigger trigger) {
        return "";
    }

    private String getElementsSet(Trigger trigger) {
        if (trigger.getType().equals(Trigger.TriggerType.BUTTON_CLICK)) {
            return "document.querySelectorAll(\"button\")";
        }
        return "";
    }

    private String getBeforeTriggerAttributes(Trigger trigger) {
        return switch (trigger.getType()) {
            case CLICK, BUTTON_CLICK -> "'click', ";
            case SCROLL -> "'scroll', ";
            default -> "";
        };
    }

    private String getAfterTriggerAttributes(Trigger trigger) {
        if (trigger.getType().equals(Trigger.TriggerType.SET_INTERVAL)) {
            return ", {delayMillis}";
        }
        return "";
    }

    private String getElementName(Trigger trigger) {
        if (trigger.getType().equals(Trigger.TriggerType.BUTTON_CLICK)) {
            return "eventObject.target.className";
        }
        return "null";
    }


    public TriggerScriptGenerator(Trigger trigger, UserInfoRepository userInfoRepository, ObjectMapper objectMapper) throws JsonProcessingException {
        this.validateTrigger(trigger);
        this.setCodeTemplate(trigger);

        final var attributes = trigger.getAttributes().getSetTimeout();
        final var userIds = userInfoRepository.findAllUserIds();

        // request settings
        final String serverUrl = "http://localhost:8080/";
        final String apiMethodName = "api/message";
        final String requestUrl = serverUrl + apiMethodName;

        String primaryFunction = getPrimaryFunction(trigger);
        String beforePrimaryFunction = getBeforePrimaryFunction(trigger);
        String afterPrimaryFunction = getAfterPrimaryFunction(trigger);

        String beforeTriggerAttributes = getBeforeTriggerAttributes(trigger);
        String afterTriggerAttributes = getAfterTriggerAttributes(trigger);

        String elementName = getElementName(trigger);

        this.codeTemplate = this.codeTemplate
                .replaceAll("\\{triggerName}", trigger.getName())
                .replaceAll("\\{requestUrl}", requestUrl)
                .replaceAll("\\{eventAttributes}", objectMapper.writeValueAsString(attributes.getMessageToSend()))
                .replaceAll("\\{appName}", trigger.getContainer().getApp().getName())
                .replaceAll("\\{appId}", String.valueOf(trigger.getContainer().getApp().getId()))
                .replaceAll("\\{userId}", userIds.get(ThreadLocalRandom.current().nextInt(userIds.size())))
                .replaceAll("\\{beforePrimaryFunction}", beforePrimaryFunction)
                .replaceAll("\\{primaryFunction}", primaryFunction)
                .replaceAll("\\{afterPrimaryFunction}", afterPrimaryFunction)
                .replaceAll("\\{beforeTriggerAttributes}", beforeTriggerAttributes)
                .replaceAll("\\{afterTriggerAttributes}", afterTriggerAttributes)
                .replaceAll("\\{elementName}", elementName);

        if (this.targetType.equals(TriggerTargetType.MULTIPLE_TARGET)) {
            String elementsSet = this.getElementsSet(trigger);
            this.codeTemplate = this.codeTemplate
                    .replaceAll("\\{elementsSet}", elementsSet);
        }
        if (trigger.getType().equals(Trigger.TriggerType.SET_INTERVAL)) {
            this.codeTemplate = this.codeTemplate
                    .replaceAll("\\{delayMillis}", String.valueOf(attributes.getDelayMillis()));
        }
    }

    public String getJavaScript() {
        return codeTemplate;
    }
}
