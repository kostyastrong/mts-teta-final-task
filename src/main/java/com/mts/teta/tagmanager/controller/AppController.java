package com.mts.teta.tagmanager.controller;

import com.mts.teta.tagmanager.controller.dto.AppCreatedResponse;
import com.mts.teta.tagmanager.controller.dto.AppResponse;
import com.mts.teta.tagmanager.domain.App;
import com.mts.teta.tagmanager.repository.AppRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppController {

  private final AppRepository appRepository;

  // Получить список существующих приложений
  // GET /api/app
  @GetMapping
  public List<AppResponse> getApps() {
    return appRepository.findAll()
        .stream()
        .map(AppResponse::new)
        .toList();
  }

  // Создать новое приложение
  // POST /api/app?name=appName
  @PostMapping
  @Transactional
  public AppCreatedResponse createApp(@RequestParam String name) {
    final var app = appRepository.save(App.newApp(name));
    return new AppCreatedResponse(app.getId());
  }
}
