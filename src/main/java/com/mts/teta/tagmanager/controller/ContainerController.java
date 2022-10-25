package com.mts.teta.tagmanager.controller;

import com.mts.teta.tagmanager.controller.dto.ContainerCreatedResponse;
import com.mts.teta.tagmanager.controller.dto.ContainerResponse;
import com.mts.teta.tagmanager.domain.Container;
import com.mts.teta.tagmanager.repository.AppRepository;
import com.mts.teta.tagmanager.repository.ContainerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/container")
@RequiredArgsConstructor
public class ContainerController {

  private final AppRepository appRepository;
  private final ContainerRepository containerRepository;

  // получить список контейнеров вместе с их триггерами по ID-шнику приложения
  // GET /api/container/app/1
  @GetMapping("/app/{appId}")
  public List<ContainerResponse> getContainers(@PathVariable long appId) {
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
}
