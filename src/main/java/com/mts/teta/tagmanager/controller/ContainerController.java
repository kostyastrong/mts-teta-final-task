package com.mts.teta.tagmanager.controller;

import static com.mts.teta.tagmanager.domain.Trigger.TriggerType.SET_INTERVAL;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.teta.tagmanager.controller.dto.ContainerCreatedResponse;
import com.mts.teta.tagmanager.controller.dto.ContainerResponse;
import com.mts.teta.tagmanager.domain.Container;
import com.mts.teta.tagmanager.domain.Trigger;
import com.mts.teta.tagmanager.repository.AppRepository;
import com.mts.teta.tagmanager.repository.ContainerRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/container")
@RequiredArgsConstructor
@CrossOrigin("*")
@Validated
public class ContainerController {

  private final AppRepository appRepository;
  private final ContainerRepository containerRepository;
  private final ObjectMapper objectMapper;

  // получить список контейнеров вместе с их триггерами по ID-шнику приложения
  // GET /api/container/app/1
  @GetMapping("/app/{appId}")
  public List<ContainerResponse> getContainers(@NotNull @PathVariable long appId) {
    return containerRepository.findAllByAppId(appId)
        .stream()
        .map(ContainerResponse::new)
        .toList();
  }

  // Создать контейнер для заданного приложения
  // POST /api/container/app/1?name=containerName
  @PostMapping("/app/{appId}")
  @Transactional
  public ContainerCreatedResponse createContainer(
      @PathVariable long appId,
      @RequestParam String name
  ) {
    final var app = appRepository.findById(appId).orElseThrow();
    final var container = containerRepository.save(Container.newContainer(name, app));
    return new ContainerCreatedResponse(container.getId());
  }

  @GetMapping(value = "/{containerId}/jsFile", produces = "text/javascript")
  @Transactional
  public byte[] getContainerAsJsFile(@NotNull@PathVariable long containerId) {
    final var container = containerRepository.findById(containerId).orElseThrow();
    final var jsFile = container.getTriggers()
        .stream()
        .map(this::triggerToJsString)
        .collect(Collectors.joining(";\n"));
    return jsFile.getBytes(UTF_8);
  }

  @SneakyThrows
  private String triggerToJsString(Trigger trigger) {
    if (trigger.getType() != SET_INTERVAL) {
      // Если будете добавлять новые типы триггеров, поддержку для них вам нужно будет
      // реализовать самостоятельно
      throw new UnsupportedOperationException(
          "Указанный тип триггера еще не поддерживается: " + trigger.getType()
      );
    }
    final var attributes = trigger.getAttributes().getSetTimeout();
    return String.format("""
            setInterval(%s, function() {
                console.log("Trigger %s is called");
                // здесь отправляется сообщение на бэкенд
                // Endpoint, как видите, захардкожен. При дефолтных настройках все будет работать.
                // Но лучше, если это поле будет конфигурируемым
                fetch('http://localhost:8080/api/message', {
                    method: 'POST',
                    mode: 'no-cors',
                    headers: {
                      'Content-Type': 'application/json'
                    },
                    // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
                    body: JSON.stringify({
                        "userId": "user_id",
                        "event": "set_interval",
                        "element": null, // setInterval не привязан к какому-то конкретному элементу на странице
                        // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                        "app_name": "",
                        "app_id": ""
                        // в event_params как раз сохраняет trigger.attributes
                        "event_params": %s
                    })
                  })
            })
            """,
        attributes.getDelayMillis(),
        trigger.getName(),
        // Здесь мы преобразуем Map<String, Object> в JSON, который и подставится в JSON.stringify
        objectMapper.writeValueAsString(
            attributes.getMessageToSend()
        )
    );
  }
}
