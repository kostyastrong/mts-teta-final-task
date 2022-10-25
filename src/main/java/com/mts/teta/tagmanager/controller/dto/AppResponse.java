package com.mts.teta.tagmanager.controller.dto;

import com.mts.teta.tagmanager.domain.App;
import lombok.Getter;

@Getter
public class AppResponse {

  private final long id;
  private final String name;

  public AppResponse(App app) {
    this.id = app.getId();
    this.name = app.getName();
  }
}
