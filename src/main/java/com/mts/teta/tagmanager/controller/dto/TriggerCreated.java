package com.mts.teta.tagmanager.controller.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TriggerCreated {
  @NotNull
  private final long id;
}
