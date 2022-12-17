package com.mts.teta.tagmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mts.teta.enricher.cache.UserInfoRepository;
import com.mts.teta.tagmanager.controller.dto.ContainerCreatedResponse;
import com.mts.teta.tagmanager.controller.dto.ContainerResponse;
import com.mts.teta.tagmanager.domain.Container;
import com.mts.teta.tagmanager.domain.Trigger;
import com.mts.teta.tagmanager.domain.TriggerConstructor.TriggerScriptGenerator;
import com.mts.teta.tagmanager.repository.AppRepository;
import com.mts.teta.tagmanager.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/api/container")
@RequiredArgsConstructor
@CrossOrigin("*")
@Validated
public class ContainerController {

    private final AppRepository appRepository;
    private final ContainerRepository containerRepository;
    private final ObjectMapper objectMapper;
    private final UserInfoRepository userInfoRepository;

    // получить список контейнеров вместе с их триггерами по ID-шнику приложения
    // GET /api/container/app/1
    @GetMapping("/app/{appId}")
    public List<ContainerResponse> getContainers(@NotNull @PathVariable long appId) {
        return containerRepository.findAllByAppId(appId).stream().map(ContainerResponse::new).toList();
    }

    // Создать контейнер для заданного приложения
    // POST /api/container/app/1?name=containerName
    @PostMapping("/app/{appId}")
    @Transactional
    public ContainerCreatedResponse createContainer(@PathVariable long appId, @RequestParam String name) {
        final var app = appRepository.findById(appId).orElseThrow();
        final var container = containerRepository.save(Container.newContainer(name, app));
        return new ContainerCreatedResponse(container.getId());
    }

    @GetMapping(value = "/{containerId}/jsFile", produces = "text/javascript;charset=UTF-8")
    @Transactional
    public byte[] getContainerAsJsFile(@NotNull @PathVariable long containerId) {
        final var container = containerRepository.findById(containerId).orElseThrow();
        final var jsFile = container.getTriggers().stream().map(this::triggerToJsString).collect(Collectors.joining(";\n"));
        return jsFile.getBytes(UTF_8);
    }

    @SneakyThrows
    private String triggerToJsString(Trigger trigger) {
        TriggerScriptGenerator triggerScriptGenerator = new TriggerScriptGenerator(trigger, userInfoRepository, objectMapper);
        return triggerScriptGenerator.getJavaScript();
    }
}