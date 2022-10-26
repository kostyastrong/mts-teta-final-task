package com.mts.teta.tagmanager.controller.dto;

import com.mts.teta.tagmanager.domain.App;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AppResponse {

  @NotNull
  private final long id;
  @NotNull
  private final String name;

  public AppResponse(App app) {
    this.id = app.getId();
    this.name = app.getName();
  }
}
