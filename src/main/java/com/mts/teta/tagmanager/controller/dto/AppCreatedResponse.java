package com.mts.teta.tagmanager.controller.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AppCreatedResponse {

  @NotNull
  private final long id;
}
